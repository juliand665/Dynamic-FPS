package dynamic_fps.impl;

import dynamic_fps.impl.compat.ClothConfig;
import dynamic_fps.impl.compat.GLFW;
import dynamic_fps.impl.config.BatteryTrackerConfig;
import dynamic_fps.impl.config.Config;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.config.option.GraphicsState;
import dynamic_fps.impl.service.ModCompat;
import dynamic_fps.impl.feature.battery.BatteryToast;
import dynamic_fps.impl.feature.battery.BatteryTracker;
import dynamic_fps.impl.feature.state.IdleHandler;
import dynamic_fps.impl.util.BatteryUtil;
import dynamic_fps.impl.util.FallbackConfigScreen;
import dynamic_fps.impl.util.Logging;
import dynamic_fps.impl.feature.state.OptionHolder;
import dynamic_fps.impl.util.ResourceLocations;
import dynamic_fps.impl.util.Version;
import dynamic_fps.impl.feature.volume.SmoothVolumeHandler;
import dynamic_fps.impl.util.duck.DuckLoadingOverlay;
import dynamic_fps.impl.feature.state.WindowObserver;
import dynamic_fps.impl.service.Platform;
import net.lostluma.battery.api.State;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dynamic_fps.impl.util.Localization.localized;

public class DynamicFPSMod {
	private static Config config = Config.ACTIVE;
	private static PowerState state = PowerState.FOCUSED;

	private static boolean isForcingLowFPS = false;
	private static boolean isKeybindDisabled = false;

	private static final Minecraft minecraft = Minecraft.getInstance();

	private static @Nullable WindowObserver window;

	private static long lastRender;

	// we always render one last frame before actually reducing FPS, so the hud text
	// shows up instantly when forcing low fps.
	// additionally, this would enable mods which render differently while mc is
	// inactive.
	private static boolean hasRenderedLastFrame = false;

	private static final boolean OVERLAY_OPTIMIZATION_ACTIVE = !ModCompat.getInstance().disableOverlayOptimization();

	// Internal "API" for Dynamic FPS itself

	public static void init() {
		doInit();

		Platform platform = Platform.getInstance();
		Version version = platform.getModVersion(Constants.MOD_ID).orElseThrow();

		Logging.getLogger().info("Dynamic FPS {} active on {}!", version, platform.getName());
	}

	public static boolean disabledByUser() {
		return isKeybindDisabled;
	}

	public static @Nullable WindowObserver getWindow() {
		return window;
	}

	public static boolean isDisabled() {
		return isKeybindDisabled || !DynamicFPSConfig.INSTANCE.enabled() || ModCompat.getInstance().isDisabled();
	}

	public static String whyIsTheModNotWorking() {
		List<String> results = new ArrayList<>();

		if (isKeybindDisabled) {
			results.add("keybinding");
		}

		if (!DynamicFPSConfig.INSTANCE.enabled()) {
			results.add("mod config");
		}

		if (ModCompat.getInstance().isDisabled()) {
			results.add("another mod");
		}

		return String.join(", ", results);
	}

	public static void toggleDisabled() {
		isKeybindDisabled = !isKeybindDisabled;
		onStatusChanged(true);
	}

	public static void onConfigChanged() {
		doInit();
		DynamicFPSConfig.INSTANCE.save();
		checkForStateChanges(); // The unplugged state may now be enabled or disabled
	}

	public static Screen getConfigScreen(Screen parent) {
		if (!Platform.getInstance().isModLoaded("cloth-config", "cloth_config")) {
			return new FallbackConfigScreen(parent);
		} else {
			return ClothConfig.genConfigScreen(parent);
		}
	}

	public static void onStatusChanged(boolean userInitiated) {
		// Ensure game runs at full speed when
		// Returning without giving any other input
		if (userInitiated) {
			IdleHandler.onActivity();
		}

		checkForStateChanges();
	}

	public static PowerState powerState() {
		return state;
	}

	public static boolean isForcingLowFPS() {
		return isForcingLowFPS;
	}

	public static void toggleForceLowFPS() {
		isForcingLowFPS = !isForcingLowFPS;
		onStatusChanged(true);
	}

	public static void setWindow(long address) {
		IdleHandler.setWindow(address);
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

	public static float volumeMultiplier(SoundSource source) {
		return config.volumeMultiplier(source);
	}

	public static boolean shouldShowToasts() {
		return config.showToasts();
	}

	public static boolean shouldShowLevels() {
		return isDisabled() || !isLevelCoveredByOverlay();
	}

	public static void onBatteryChargeChanged(int before, int after) {
		if (before > 10 && after <= 10) {
			showNotification("battery_critical", "reminder");
		}
	}

	public static void onBatteryStatusChanged(State before, State after) {
		if (before == State.DISCHARGING && BatteryUtil.isCharging(after)) {
			showNotification("battery_charging", "charging");
		} else if (BatteryUtil.isCharging(before) && after == State.DISCHARGING) {
			showNotification("battery_draining", "draining");
		}
	}

	// Internal logic

	private static void doInit() {
		// NOTE: Init battery tracker first here
		// Since the idle handler queries it for info
		BatteryTracker.init();
		IdleHandler.init();
		SmoothVolumeHandler.init();
	}

	private static void showNotification(String titleTranslationKey, String iconPath) {
		if (!DynamicFPSConfig.INSTANCE.batteryTracker().notifications()) {
			return;
		}

		Component title = localized("toast", titleTranslationKey);
		ResourceLocation icon = ResourceLocations.of("dynamic_fps", "textures/battery/toast/" + iconPath + ".png");

		BatteryToast.queueToast(title, icon);
	}

	private static boolean isLevelCoveredByOverlay() {
		return OVERLAY_OPTIMIZATION_ACTIVE && minecraft.getOverlay() instanceof LoadingOverlay && ((DuckLoadingOverlay)minecraft.getOverlay()).dynamic_fps$isReloadComplete();
	}

	@SuppressWarnings("squid:S1215") // Garbage collector call
	public static void handleStateChange(PowerState previous, PowerState current) {
		if (Constants.DEBUG) {
			Logging.getLogger().info("State changed from {} to {}.", previous, current);
		}

		Config before = config;
		config = DynamicFPSConfig.INSTANCE.get(current);

		GLFW.applyWorkaround(); // Apply mouse hover fix if required
		hasRenderedLastFrame = false; // Render next frame w/o delay

		if (config.runGarbageCollector()) {
			System.gc();
		}

		SmoothVolumeHandler.onStateChange();

		if (before.graphicsState() != config.graphicsState()) {
			if (before.graphicsState() == GraphicsState.DEFAULT) {
				OptionHolder.copyOptions(minecraft.options);
			}

			OptionHolder.applyOptions(minecraft.options, config.graphicsState());
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
		BatteryTrackerConfig batteryTracking = DynamicFPSConfig.INSTANCE.batteryTracker();

		if (isDisabled()) {
			current = PowerState.FOCUSED;
		} else if (isForcingLowFPS) {
			current = PowerState.UNFOCUSED;
		} else if (window.isFocused()) {
			if (IdleHandler.isIdle()) {
				current = PowerState.ABANDONED;
			} else if (batteryTracking.enabled() && batteryTracking.switchStates() && BatteryTracker.status() == State.DISCHARGING) {
				current = PowerState.UNPLUGGED;
			} else {
				current = PowerState.FOCUSED; // Default
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
		int frameRateTarget = targetFrameRate();

		// Disable all rendering
		if (frameRateTarget == 0) {
			return false;
		}

		// Disable frame rate limiting
		if (frameRateTarget == Constants.NO_FRAME_RATE_LIMIT) {
			return true;
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
