package dynamic_fps.impl.util.duck;

import dynamic_fps.impl.config.Config;
import net.minecraft.sounds.SoundSource;

public interface DuckSoundEngine {
	public default void dynamic_fps$updateVolume(Config before, SoundSource source) {
		throw new RuntimeException("No implementation for dynamic_fps$updateVolume was found.");
	}
}
