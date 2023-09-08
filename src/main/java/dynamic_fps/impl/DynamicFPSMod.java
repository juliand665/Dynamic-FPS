package dynamic_fps.impl;

import dynamic_fps.impl.compat.FREX;
import dynamic_fps.impl.config.Config;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.util.HudInfoRenderer;
import dynamic_fps.impl.util.KeyMappingHandler;
import dynamic_fps.impl.util.Logging;
import dynamic_fps.impl.util.ModCompatibility;
import dynamic_fps.impl.util.OptionsHolder;
import dynamic_fps.impl.util.WindowObserver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.sounds.SoundSource;

import static dynamic_fps.impl.util.Localization.translationKey;

public class DynamicFPSMod implements ClientModInitializer {
	public static final String MOD_ID = "dynamic_fps";
	public static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

	private static Config config = Config.ACTIVE;
	private static PowerState state = PowerState.FOCUSED;

	public static DynamicFPSConfig modConfig = DynamicFPSConfig.load();

	private static boolean isDisabled = false;
	private static boolean isForcingLowFPS = false;

	private static Minecraft minecraft;
	private static WindowObserver window;

	private static long lastRender;

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
				onStatusChanged();
			});

	private static final KeyMappingHandler toggleDisabledKeyBinding = new KeyMappingHandler(
			translationKey("key", "toggle_disabled"),
			"key.categories.misc",
			() -> {
				isDisabled = !isDisabled;
				onStatusChanged();
			});

	@Override
	public void onInitializeClient() {
		modConfig.save(); // Force create file on disk

		toggleForcedKeyBinding.register();
		toggleDisabledKeyBinding.register();

		HudRenderCallback.EVENT.register(new HudInfoRenderer());
	}

	// Internal "API" for Dynamic FPS itself

	public static boolean isDisabled() {
		return isDisabled;
	}

	public static void onStatusChanged() {
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

	private static boolean isPauseScreenOpened() {
		return minecraft.screen instanceof PauseScreen;
	}

	private static boolean isLevelCoveredByScreen() {
		return minecraft.screen != null && minecraft.screen.dynamic_fps$rendersBackground();
	}

	private static boolean isLevelCoveredByOverlay() {
		return OVERLAY_OPTIMIZATION_ACTIVE && minecraft.getOverlay() instanceof LoadingOverlay loadingOverlay && loadingOverlay.dynamic_fps$isReloadComplete();
	}

	@SuppressWarnings("squid:S1215") // Garbage collector call
	public static void handleStateChange(PowerState previous, PowerState current) {
		if (DEBUG) {
			Logging.getLogger().info("State changed from {} to {}.", previous, current);
		}

		var before = config;
		config = modConfig.get(current);

		hasRenderedLastFrame = false; // Render next frame w/o delay

		if (config.runGarbageCollector()) {
			System.gc();
		}

		if (before.volumeMultiplier() != config.volumeMultiplier()) {
			setVolumeMultiplier(config.volumeMultiplier());
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

		if (minecraft == null) {
			minecraft = Minecraft.getInstance();
		}

		PowerState current;

		if (isDisabledInternal()) {
			current = PowerState.FOCUSED;
		} else if (isForcingLowFPS) {
			current = PowerState.UNFOCUSED;
		} else if (window.isFocused()) {
			if (!isPauseScreenOpened()) {
				current = PowerState.FOCUSED;
			} else {
				current = PowerState.SUSPENDED;
			}
		} else if (window.isHovered()) {
			current = PowerState.HOVERED;
		} else if (!window.isIconified()) {
			current = PowerState.UNFOCUSED;
		} else {
			current = PowerState.INVISIBLE;
		}

		if (state != current) {
			var previous = state;
			state = current;

			handleStateChange(previous, current);
		}
	}

	private static boolean willPauseSounds() {
		return !minecraft.isWindowActive() && minecraft.options.pauseOnLostFocus && minecraft.screen == null;
	}

	private static void setVolumeMultiplier(float multiplier) {
		// setting the volume to 0 stops all sounds (including music), which we want to
		// avoid if possible.
		// if the client would pause anyway, we don't need to do anything because that
		// will already pause all sounds.
		if (multiplier == 0 && willPauseSounds())
			return;

		var baseVolume = minecraft.options.getSoundSourceVolume(SoundSource.MASTER);
		minecraft.getSoundManager().updateSourceVolume(
			SoundSource.MASTER,
			baseVolume * multiplier
		);
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
