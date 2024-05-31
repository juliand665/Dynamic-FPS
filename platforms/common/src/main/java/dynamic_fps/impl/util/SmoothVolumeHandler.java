package dynamic_fps.impl.util;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.duck.DuckSoundEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.Map;

public class SmoothVolumeHandler {
	private static boolean active = false;
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static final Map<SoundSource, Float> currentOverrides = new HashMap<>();

	public static void init() {
		if (active || !DynamicFPSMod.volumeTransitionSpeed().isActive()) {
			return;
		}

		active = true;
		Platform.getInstance().registerStartTickEvent(SmoothVolumeHandler::tickVolumes);
	}

	public static float volumeMultiplier(SoundSource source) {
		if (!active) {
			return DynamicFPSMod.volumeMultiplier(source);
		} else {
			return currentOverrides.getOrDefault(source, 1.0f);
		}
	}

	private static void tickVolumes() {
		for (SoundSource source : SoundSource.values()) {
			float desired = DynamicFPSMod.volumeMultiplier(source);
			float current = currentOverrides.getOrDefault(source, 1.0f);

			if (current != desired) {
				if (current < desired) {
					float up = DynamicFPSMod.volumeTransitionSpeed().getUp();
					currentOverrides.put(source, Math.min(desired, current + up / 20.0f));
				} else {
					float down = DynamicFPSMod.volumeTransitionSpeed().getDown();
					currentOverrides.put(source, Math.max(desired, current - down / 20.0f));
				}

				// Update volume of currently playing sounds
				((DuckSoundEngine) minecraft.getSoundManager().soundEngine).dynamic_fps$updateVolume(source);
			}
		}
	}
}
