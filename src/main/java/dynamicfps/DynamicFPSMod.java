package dynamicfps;

import dynamicfps.util.KeyBindingHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.glfw.Window;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.concurrent.locks.LockSupport;

import static dynamicfps.util.Localization.translationKey;

public class DynamicFPSMod implements ModInitializer {
	public static final String MOD_ID = "dynamicfps";

	public static DynamicFPSConfig config = null;

	private static boolean isDisabled = false;
	public static boolean isDisabled() { return isDisabled; }

	private static boolean isForcingLowFPS = false;
	public static boolean isForcingLowFPS() { return isForcingLowFPS; }

	private static final KeyBindingHandler toggleForcedKeyBinding = new KeyBindingHandler(
		translationKey("key", "toggle_forced"),
		"key.categories.misc",
		() -> isForcingLowFPS = !isForcingLowFPS
	);

	private static final KeyBindingHandler toggleDisabledKeyBinding = new KeyBindingHandler(
		translationKey("key", "toggle_disabled"),
		"key.categories.misc",
		() -> isDisabled = !isDisabled
	);

	@Override
	public void onInitialize() {
		config = DynamicFPSConfig.load();

		toggleForcedKeyBinding.register();
		toggleDisabledKeyBinding.register();

		HudRenderCallback.EVENT.register(new HudInfoRenderer());
		FlawlessFrames.onClientInitialization();
	}

	private static MinecraftClient client;
	private static Window window;
	private static boolean isFocused, isVisible, isHovered;
	private static long lastRender;
	private static CloudRenderMode previousCloudRenderMode;
	private static GraphicsMode previousGraphicsMode;
	private static boolean previousSmoothLighting;
	private static ParticlesMode previousParticlesMode;
	private static boolean previousEntityShadows;
	private static double previousEntityDistance;
	/**
	 Determines whether the game should render anything at this time. If not, blocks for a short time.

	 @return whether the game should be rendered after this.
	 */
	public static boolean checkForRender() {
		if (isDisabled || FlawlessFrames.isActive()) return true;

		if (client == null) {
			client = MinecraftClient.getInstance();
			window = client.getWindow();
		}
		isFocused = client.isWindowFocused();
		isVisible = GLFW.glfwGetWindowAttrib(window.getHandle(), GLFW.GLFW_VISIBLE) != 0;
		isHovered = GLFW.glfwGetWindowAttrib(window.getHandle(), GLFW.GLFW_HOVERED) != 0;

		checkForStateChanges();

		long currentTime = Util.getMeasuringTimeMs();
		long timeSinceLastRender = currentTime - lastRender;

		if (!checkForRender(timeSinceLastRender)) return false;

		lastRender = currentTime;
		return true;
	}

	public static boolean shouldShowToasts() {
		return isDisabled || FlawlessFrames.isActive() || fpsOverride() == null;
	}

	public static void reduceGraphics(GameOptions gameOptions) {
		previousCloudRenderMode = gameOptions.getCloudRenderMode();
		gameOptions.getRenderClouds().set(CloudRenderMode.OFF);
		previousParticlesMode = gameOptions.getParticles().get();
		gameOptions.getParticles().set(ParticlesMode.MINIMAL);
		previousEntityShadows = gameOptions.getEntityShadows().get();
		gameOptions.getEntityShadows().set(false);
		previousEntityDistance = gameOptions.getEntityDistanceScaling().get();
		gameOptions.getEntityDistanceScaling().set(0.5);
	}

	public static void increaseGraphics(GameOptions gameOptions) {
		gameOptions.getRenderClouds().set(previousCloudRenderMode);
		gameOptions.getParticles().set(previousParticlesMode);
		gameOptions.getEntityShadows().set(previousEntityShadows);
		gameOptions.getEntityDistanceScaling().set(previousEntityDistance);
	}

	public static void reduceGraphicsFully(GameOptions gameOptions) {
		previousGraphicsMode = gameOptions.getGraphicsMode().get();
		gameOptions.getGraphicsMode().set(GraphicsMode.FAST);
		previousSmoothLighting = gameOptions.getSmoothLighting().get();
		gameOptions.getSmoothLighting().set(false);
	}

	public static void increaseGraphicsFully(GameOptions gameOptions) {
		gameOptions.getGraphicsMode().set(previousGraphicsMode);
		gameOptions.getSmoothLighting().set(previousSmoothLighting);
	}

	private static boolean wasFocused = true;
	private static boolean wasVisible = true;
	private static void checkForStateChanges() {
		if (isFocused != wasFocused) {
			wasFocused = isFocused;
			if (isFocused) {
				onFocus();
			} else {
				onUnfocus();
			}
		}

		if (isVisible != wasVisible) {
			wasVisible = isVisible;
			if (isVisible) {
				onAppear();
			} else {
				onDisappear();
			}
		}
	}

	private static void onFocus() {
		setVolumeMultiplier(1);
		GameOptions gameOptions = MinecraftClient.getInstance().options;
		increaseGraphicsFully(gameOptions);
		increaseGraphics(gameOptions);
	}

	private static void onUnfocus() {
		GameOptions gameOptions = MinecraftClient.getInstance().options;
		if(config.fullyReduceGraphicsWhenUnfocused){
			reduceGraphicsFully(gameOptions);
		}
		if(config.reduceGraphicsWhenUnfocused) {
			reduceGraphics(gameOptions);
		}


		if (isVisible) {
			setVolumeMultiplier(config.unfocusedVolumeMultiplier);
		}

		if (config.runGCOnUnfocus) {
			System.gc();
		}
	}

	private static void onAppear() {
		if (!isFocused) {
			setVolumeMultiplier(config.unfocusedVolumeMultiplier);
		}
	}

	private static void onDisappear() {
		setVolumeMultiplier(config.hiddenVolumeMultiplier);
	}

	private static void setVolumeMultiplier(float multiplier) {
		// setting the volume to 0 stops all sounds (including music), which we want to avoid if possible.
		var clientWillPause = !isFocused && client.options.pauseOnLostFocus && client.currentScreen == null;
		// if the client would pause anyway, we don't need to do anything because that will already pause all sounds.
		if (multiplier == 0 && clientWillPause) return;

		var baseVolume = client.options.getSoundVolume(SoundCategory.MASTER);
		client.getSoundManager().updateSoundVolume(
			SoundCategory.MASTER,
			baseVolume * multiplier
		);
	}

	// we always render one last frame before actually reducing FPS, so the hud text shows up instantly when forcing low fps.
	// additionally, this would enable mods which render differently while mc is inactive.
	private static boolean hasRenderedLastFrame = false;
	private static boolean checkForRender(long timeSinceLastRender) {
		Integer fpsOverride = fpsOverride();
		if (fpsOverride == null) {
			hasRenderedLastFrame = false;
			return true;
		}

		if (!hasRenderedLastFrame) {
			// render one last frame before reducing, to make sure differences in this state show up instantly.
			hasRenderedLastFrame = true;
			return true;
		}

		if (fpsOverride == 0) {
			idle(1000);
			return false;
		}

		long frameTime = 1000 / fpsOverride;
		boolean shouldSkipRender = timeSinceLastRender < frameTime;
		if (!shouldSkipRender) return true;

		idle(frameTime);
		return false;
	}

	/**
	 force minecraft to idle because otherwise we'll be busy checking for render again and again
	 */
	private static void idle(long waitMillis) {
		// cap at 30 ms before we check again so user doesn't have to wait long after tabbing back in
		waitMillis = Math.min(waitMillis, 30);
		LockSupport.parkNanos("waiting to render", waitMillis * 1_000_000);
	}

	@Nullable
	private static Integer fpsOverride() {
		if (!isVisible) return 0;
		if (isForcingLowFPS) return config.unfocusedFPS;
		if (config.restoreFPSWhenHovered && isHovered) return null;
		if (config.reduceFPSWhenUnfocused && !client.isWindowFocused()) return config.unfocusedFPS;
		return null;
	}
}
