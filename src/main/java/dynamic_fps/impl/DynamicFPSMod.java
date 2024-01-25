package dynamic_fps.impl;

import dynamic_fps.impl.compat.FREX;
import dynamic_fps.impl.compat.GLFW;
import dynamic_fps.impl.config.Config;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.util.HudInfoRenderer;
import dynamic_fps.impl.util.KeyMappingHandler;
import dynamic_fps.impl.util.Logging;
import dynamic_fps.impl.util.ModCompatibility;
import dynamic_fps.impl.util.OptionsHolder;
import dynamic_fps.impl.util.event.InputObserver;
import dynamic_fps.impl.util.event.WindowObserver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.sounds.SoundSource;

import static dynamic_fps.impl.util.Localization.translationKey;

import org.jetbrains.annotations.Nullable;

public class DynamicFPSMod implements ClientModInitializer {
	public static final String MOD_ID = "dynamic_fps";
	public static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

	private static Config config = Config.ACTIVE;
	private static PowerState state = PowerState.FOCUSED;

	public static DynamicFPSConfig modConfig = DynamicFPSConfig.load();

	private static boolean isDisabled = false;
	private static boolean isForcingLowFPS = false;

	private static final Minecraft minecraft = Minecraft.getInstance();

	private static @Nullable WindowObserver window;
	private static @Nullable InputObserver devices;

	private static long lastRender;

	private static boolean wasIdle = false;
	private static boolean idleCheckRegistered = false;

	// we always render one last frame before actually reducing FPS, so the hud text
	// shows up instantly when forcing low fps.
	// additionally, this would enable mods which render differently while mc is
	// inactive.
	private static boolean hasRenderedLastFrame = false;

	private static final boolean OVERLAY_OPTIMIZATION_ACTIVE = !ModCompatibility.disableOverlayOptimization();

	private static final KeyMappingHandler toggleForcedKeyBinding = new KeyMappingHandler(
			translationKey("key", "toggle_forced"),
			"key.categories.misc",
			() -> {
				isForcingLowFPS = !isForcingLowFPS;
				onStatusChanged(true);
			});

	private static final KeyMappingHandler toggleDisabledKeyBinding = new KeyMappingHandler(
			translationKey("key", "toggle_disabled"),
			"key.categories.misc",
			() -> {
				isDisabled = !isDisabled;
				onStatusChanged(true);
			});

	@Override
	public void onInitializeClient() {
		toggleForcedKeyBinding.register();
		toggleDisabledKeyBinding.register();

		initializeIdleCheck();
		HudRenderCallback.EVENT.register(new HudInfoRenderer());
	}

	// Internal "API" for Dynamic FPS itself

	public static boolean isDisabled() {
		return isDisabled;
	}

	public static void onConfigChanged() {
		modConfig.save();
		initializeIdleCheck();
	}

	public static void onStatusChanged(boolean userInitiated) {
		// Reset idle timeout to ensure
		// The game runs at full speed when
		// Returning but not giving any input
		if (userInitiated && devices != null) {
			devices.updateLastActionTime();
		}

		checkForStateChanges();
	}

	public static PowerState powerState() {
		return state;
	}

	public static boolean isForcingLowFPS() {
		return isForcingLowFPS;
	}

	public static void setWindow(long address) {
		window = new WindowObserver(address);
		initializeIdleCheck(); // Register input observer if wanted
	}

	public static boolean checkForRender() {
		long currentTime = Util.getEpochMillis();
		long timeSinceLastRender = currentTime - lastRender;

		if (!checkForRender(timeSinceLastRender)) {
			return false;
		}

		lastRender = currentTime;
		return true;
	}

	public static int targetFrameRate() {
		return config.frameRateTarget();
	}

	public static float volumeMultiplier(SoundSource source) {
		return config.volumeMultiplier(source);
	}

	public static boolean shouldShowToasts() {
		return config.showToasts();
	}

	public static boolean shouldShowLevels() {
		return isDisabledInternal() || !(isLevelCoveredByScreen() || isLevelCoveredByOverlay());
	}

	// Internal logic

	private static boolean isDisabledInternal() {
		return isDisabled || FREX.isFlawlessFramesActive();
	}

	private static boolean isLevelCoveredByScreen() {
		return minecraft.screen != null && minecraft.screen.dynamic_fps$rendersBackground();
	}

	private static boolean isIdle() {
		int idleTime = modConfig.idleTime();

		if (idleTime == 0) {
			return false;
		}

		return (Util.getEpochMillis() - devices.lastActionTime()) >= idleTime * 1000;
	}

	private static boolean isLevelCoveredByOverlay() {
		return OVERLAY_OPTIMIZATION_ACTIVE && minecraft.getOverlay() instanceof LoadingOverlay && ((LoadingOverlay)minecraft.getOverlay()).dynamic_fps$isReloadComplete();
	}

	private static void initializeIdleCheck() {
		if (idleCheckRegistered) {
			return;
		}

		if (modConfig.idleTime() == 0 || window == null) {
			return;
		}

		idleCheckRegistered = true;

		// We only register the input observer and tick handler
		// When it's used to run less unused code at all times.
		devices = new InputObserver(window.address());

		ClientTickEvents.START_CLIENT_TICK.register((minecraft) -> {
			boolean idle = isIdle();

			if (idle != wasIdle) {
				wasIdle = idle;
				onStatusChanged(false);
			}
		});
	}

	@SuppressWarnings("squid:S1215") // Garbage collector call
	public static void handleStateChange(PowerState previous, PowerState current) {
		if (DEBUG) {
			Logging.getLogger().info("State changed from {} to {}.", previous, current);
		}

		Config before = config;
		config = modConfig.get(current);

		hasRenderedLastFrame = false; // Render next frame w/o delay

		if (config.runGarbageCollector()) {
			System.gc();
		}

		if (GLFW.useHoverEventWorkaround()) {
			if (!current.windowActive) {
				minecraft.mouseHandler.releaseMouse();
			} else {
				// Grabbing the mouse only works while Minecraft
				// Agrees that the window is focused. The mod is
				// A little too fast for this, so we schedule it
				// For the next client tick (before next frame).
				minecraft.tell(minecraft.mouseHandler::grabMouse);
			}
		}

		for (SoundSource source : SoundSource.values()) {
			if (before.volumeMultiplier(source) != config.volumeMultiplier(source)) {
				minecraft.getSoundManager().soundEngine.dynamic_fps$updateVolume(source);
			}
		}

		if (before.graphicsState() != config.graphicsState()) {
			if (before.graphicsState() == GraphicsState.DEFAULT) {
				OptionsHolder.copyOptions(minecraft.options);
			}

			OptionsHolder.applyOptions(minecraft.options, config.graphicsState());
		}
	}

	private static void checkForStateChanges() {
		if (window == null) {
			return;
		}

		if (minecraft.isSameThread()) {
			checkForStateChanges0();
		} else {
			// Schedule check for the beginning of the next frame
			minecraft.tell(DynamicFPSMod::checkForStateChanges0);
		}
	}

	private static void checkForStateChanges0() {
		PowerState current;

		if (isDisabledInternal()) {
			current = PowerState.FOCUSED;
		} else if (isForcingLowFPS) {
			current = PowerState.UNFOCUSED;
		} else if (window.isFocused()) {
			if (!isIdle()) {
				current = PowerState.FOCUSED;
			} else {
				current = PowerState.ABANDONED;
			}
		} else if (window.isHovered()) {
			current = PowerState.HOVERED;
		} else if (!window.isIconified()) {
			current = PowerState.UNFOCUSED;
		} else {
			current = PowerState.INVISIBLE;
		}

		if (state != current) {
			PowerState previous = state;
			state = current;

			handleStateChange(previous, current);
		}
	}

	private static boolean checkForRender(long timeSinceLastRender) {
		int frameRateTarget = config.frameRateTarget();

		// Special frame rate target
		//  0 -> disable rendering
		// -1 -> uncapped frame rate
		if (frameRateTarget <= 0) {
			return frameRateTarget == -1;
		}

		// Render one more frame before
		// Applying the custom frame rate
		// So changes show up immediately
		if (!hasRenderedLastFrame) {
			hasRenderedLastFrame = true;
			return true;
		}

		long frameTime = 1000 / frameRateTarget;
		return timeSinceLastRender >= frameTime;
	}
}
