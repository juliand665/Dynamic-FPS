package dynamic_fps.impl;

import dynamic_fps.impl.compat.FREX;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.util.KeyMappingHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.Util;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;
import net.minecraft.sounds.SoundSource;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.Window;

import java.util.concurrent.locks.LockSupport;

import static dynamic_fps.impl.util.Localization.translationKey;

public class DynamicFPSMod implements ClientModInitializer {
	public static final String MOD_ID = "dynamic_fps";

	public static DynamicFPSConfig config = null;

	private static boolean isDisabled = false;
	public static boolean isDisabled() { return isDisabled; }

	private static boolean isForcingLowFPS = false;
	public static boolean isForcingLowFPS() { return isForcingLowFPS; }

	private static final KeyMappingHandler toggleForcedKeyBinding = new KeyMappingHandler(
		translationKey("key", "toggle_forced"),
		"key.categories.misc",
		() -> isForcingLowFPS = !isForcingLowFPS
	);

	private static final KeyMappingHandler toggleDisabledKeyBinding = new KeyMappingHandler(
		translationKey("key", "toggle_disabled"),
		"key.categories.misc",
		() -> isDisabled = !isDisabled
	);

	@Override
	public void onInitializeClient() {
		config = DynamicFPSConfig.load();

		toggleForcedKeyBinding.register();
		toggleDisabledKeyBinding.register();

		HudRenderCallback.EVENT.register(new HudInfoRenderer());
	}

	private static Minecraft client;
	private static Window window;
	private static boolean isFocused, isVisible, isHovered;
	private static long lastRender;
	private static CloudStatus previousCloudStatus;
	private static GraphicsStatus previousGraphicsStatus;
	private static boolean previousAmbientOcclusion;
	private static ParticleStatus previousParticlesStatus;
	private static boolean previousEntityShadows;
	private static double previousEntityDistance;
	/**
	 Determines whether the game should render anything at this time. If not, blocks for a short time.

	 @return whether the game should be rendered after this.
	 */
	public static boolean checkForRender() {
		if (isDisabled || FREX.isFlawlessFramesActive()) return true;

		if (client == null) {
			client = Minecraft.getInstance();
			window = client.getWindow();
		}
		isFocused = client.isWindowActive();
		isVisible = GLFW.glfwGetWindowAttrib(window.getWindow(), GLFW.GLFW_VISIBLE) != 0
			&& GLFW.glfwGetWindowAttrib(window.getWindow(), GLFW.GLFW_ICONIFIED) == 0;
		isHovered = GLFW.glfwGetWindowAttrib(window.getWindow(), GLFW.GLFW_HOVERED) != 0;

		checkForStateChanges();

		long currentTime = Util.getEpochMillis();
		long timeSinceLastRender = currentTime - lastRender;

		if (!checkForRender(timeSinceLastRender)) return false;

		lastRender = currentTime;
		return true;
	}

	public static boolean shouldShowToasts() {
		return isDisabled || FREX.isFlawlessFramesActive() || fpsOverride() == null;
	}

	public static void reduceGraphics(Options options) {
		previousCloudStatus = options.cloudStatus().get();
		options.cloudStatus().set(CloudStatus.OFF);
		previousParticlesStatus = options.particles().get();
		options.particles().set(ParticleStatus.MINIMAL);
		previousEntityShadows = options.entityShadows().get();
		options.entityShadows().set(false);
		previousEntityDistance = options.entityDistanceScaling().get();
		options.entityDistanceScaling().set(0.5);
	}

	public static void increaseGraphics(Options options) {
		options.cloudStatus().set(previousCloudStatus);
		options.particles().set(previousParticlesStatus);
		options.entityShadows().set(previousEntityShadows);
		options.entityDistanceScaling().set(previousEntityDistance);
	}

	public static void reduceGraphicsFully(Options options) {
		previousGraphicsStatus = options.graphicsMode().get();
		options.graphicsMode().set(GraphicsStatus.FAST);
		previousAmbientOcclusion = options.ambientOcclusion().get();
		options.ambientOcclusion().set(false);
	}

	public static void increaseGraphicsFully(Options options) {
		options.graphicsMode().set(previousGraphicsStatus);
		options.ambientOcclusion().set(previousAmbientOcclusion);
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
		Options options = client.options;
		if (config.fullyReduceGraphicsWhenUnfocused) {
			increaseGraphicsFully(options);
		}
		if (config.reduceGraphicsWhenUnfocused) {
			increaseGraphics(options);
		}
	}

	private static void onUnfocus() {
		Options options = client.options;
		if (config.fullyReduceGraphicsWhenUnfocused) {
			reduceGraphicsFully(options);
		}
		if (config.reduceGraphicsWhenUnfocused) {
			reduceGraphics(options);
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
		var clientWillPause = !isFocused && client.options.pauseOnLostFocus && client.screen == null;
		// if the client would pause anyway, we don't need to do anything because that will already pause all sounds.
		if (multiplier == 0 && clientWillPause) return;

		var baseVolume = client.options.getSoundSourceVolume(SoundSource.MASTER);
		client.getSoundManager().updateSourceVolume(
			SoundSource.MASTER,
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
		if (config.reduceFPSWhenUnfocused && !client.isWindowActive()) return config.unfocusedFPS;
		return null;
	}
}
