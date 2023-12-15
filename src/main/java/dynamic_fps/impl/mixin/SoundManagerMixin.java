package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dynamic_fps.impl.util.duck.DuckSoundManager;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;

@Mixin(SoundManager.class)
public class SoundManagerMixin implements DuckSoundManager {
	@Shadow
	@Final
	private SoundEngine soundEngine;

	public void dynamic_fps$updateVolume(SoundSource source) {
		this.soundEngine.dynamic_fps$updateVolume(source);
	}
}
