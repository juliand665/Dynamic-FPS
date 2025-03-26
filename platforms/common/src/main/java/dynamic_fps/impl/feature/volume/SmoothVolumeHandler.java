package dynamic_fps.impl.feature.volume;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.config.VolumeTransitionConfig;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.duck.DuckSoundEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.Map;

public class SmoothVolumeHandler {
	private static boolean active = false;
	private static boolean needsUpdating = false;

	private static final Map<SoundSource, Float> currentOverrides = new HashMap<>();

	public static void init() {
		if (active || !DynamicFPSConfig.INSTANCE.volumeTransitionSpeed().isActive()) {
			return;
		}

		active = true;
		Platform.getInstance().registerStartTickEvent(SmoothVolumeHandler::tickVolumes);
	}

	public static void onStateChange() {
		if (active) {
			needsUpdating = true;
		} else {
			for (SoundSource source : SoundSource.values()) {
				updateVolume(source);
			}
		}
	}

	public static float volumeMultiplier(SoundSource source) {
		if (!active) {
			return DynamicFPSMod.volumeMultiplier(source);
		} else {
			return currentOverrides.getOrDefault(source, 1.0f);
		}
	}

	private static void tickVolumes() {
		if (!needsUpdating) {
			return;
		}

		boolean didUpdate = false;
		VolumeTransitionConfig config = DynamicFPSConfig.INSTANCE.volumeTransitionSpeed();

		for (SoundSource source : SoundSource.values()) {
			float desired = DynamicFPSMod.volumeMultiplier(source);
			float current = currentOverrides.getOrDefault(source, 1.0f);

			if (current != desired) {
				didUpdate = true;

				if (current < desired) {
					currentOverrides.put(source, Math.min(desired, current + config.getUp() / 20.0f));
				} else {
					currentOverrides.put(source, Math.max(desired, current - config.getDown() / 20.0f));
				}

				updateVolume(source);
			}
		}

		if (!didUpdate) {
			needsUpdating = false;
		}
	}

	private static void updateVolume(SoundSource source) {
		// Update volume of currently playing sounds
		Minecraft minecraft = Minecraft.getInstance();
		((DuckSoundEngine) minecraft.getSoundManager().soundEngine).dynamic_fps$updateVolume(source);
	}
}
