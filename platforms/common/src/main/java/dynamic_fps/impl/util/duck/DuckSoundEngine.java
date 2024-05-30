package dynamic_fps.impl.util.duck;

import net.minecraft.sounds.SoundSource;

public interface DuckSoundEngine {
	default void dynamic_fps$updateVolume(SoundSource source) {
		throw new RuntimeException("No implementation for dynamic_fps$updateVolume was found.");
	}
}
