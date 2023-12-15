package dynamic_fps.impl.mixin;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.audio.Listener;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.util.duck.DuckSoundManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;

@Mixin(SoundEngine.class)
public class SoundEngineMixin implements DuckSoundManager {
	@Shadow
	private boolean loaded;

	@Shadow
	@Final
	private Listener listener;

	@Shadow
	@Final
	private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

	@Shadow
	private float getVolume(@Nullable SoundSource source) {
		throw new RuntimeException("Failed to find SoundEngine#getVolume.");
	}

	@Shadow
	private float calculateVolume(SoundInstance instance) {
		throw new RuntimeException("Failed to find SoundEngine#calculateVolume.");
	};

	public void dynamic_fps$updateVolume(SoundSource source) {
		if (!this.loaded) {
			return;
		}

		if (source.equals(SoundSource.MASTER)) {
			this.listener.setGain(this.getVolume(source));
			return;
		}

		// When setting the volume to zero we pause music but cancel other types of sounds
		// This results in a less jarring experience when quickly tabbing out and back in.
		// Also fixes this compat bug: https://github.com/juliand665/Dynamic-FPS/issues/55
		var isMusic = source.equals(SoundSource.MUSIC) || source.equals(SoundSource.RECORDS);

		this.instanceToChannel.forEach((instance, handle) -> {
			float volume = this.calculateVolume((SoundInstance) instance);

			if (instance.getSource().equals(source)) {
				handle.execute(channel -> {
					if (volume <= 0.0f) {
						if (!isMusic) {
							channel.stop();
						} else {
							channel.pause();
						}
					} else {
						channel.unpause();
						channel.setVolume(volume);
					}
				});
			}
		});
	}

	/**
	 * Cancels playing sounds while we are overwriting the volume to be off.
	 *
	 * This is done in favor of actually setting the volume to zero, because it
	 * Allows pausing and resuming the sound engine without cancelling active sounds.
	 */
	@Inject(method = { "play", "playDelayed" }, at = @At("HEAD"), cancellable = true)
	private void play(SoundInstance instance, CallbackInfo callbackInfo) {
		var master = DynamicFPSMod.volumeMultiplier(SoundSource.MASTER);
		var source = DynamicFPSMod.volumeMultiplier(instance.getSource());

		if (master == 0.0f || source == 0.0f) {
			callbackInfo.cancel();
		}
	}

	/**
	 * Applies the user's requested volume multiplier to any newly played sounds.
	 */
	@Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
	private void getVolume(@Nullable SoundSource source, CallbackInfoReturnable<Float> callbackInfo) {
		if (source != null) {
			callbackInfo.setReturnValue(callbackInfo.getReturnValue() * DynamicFPSMod.volumeMultiplier(source));
		}
	}
}
