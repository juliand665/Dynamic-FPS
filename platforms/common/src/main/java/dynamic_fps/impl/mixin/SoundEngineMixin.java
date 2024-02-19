package dynamic_fps.impl.mixin;

import java.util.Map;

import dynamic_fps.impl.config.Config;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Listener;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.util.duck.DuckSoundEngine;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;

@Mixin(SoundEngine.class)
public class SoundEngineMixin implements DuckSoundEngine {
	@Shadow
	@Final
	private Options options;

	@Shadow
	private boolean loaded;

	@Shadow
	@Final
	private Listener listener;

	@Shadow
	@Final
	private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

	@Shadow
	private float calculateVolume(SoundInstance instance) {
		throw new RuntimeException("Failed to find SoundEngine#calculateVolume.");
	};

	@Unique
	private static final Minecraft dynamic_fps$minecraft = Minecraft.getInstance();

	public void dynamic_fps$updateVolume(Config before, SoundSource source) {
		if (!this.loaded) {
			return;
		}

		if (source.equals(SoundSource.MASTER)) {
			float volume = this.options.getSoundSourceVolume(source);
			this.listener.setGain(this.adjustVolume(volume, source));
			return;
		}

		// When setting the volume to zero we pause music but cancel other types of sounds
		// This results in a less jarring experience when quickly tabbing out and back in.
		// Also fixes this compat bug: https://github.com/juliand665/Dynamic-FPS/issues/55
		boolean isMusic = source.equals(SoundSource.MUSIC) || source.equals(SoundSource.RECORDS);

		this.instanceToChannel.forEach((instance, handle) -> {
			if (instance.getSource().equals(source)) {
				float volume = this.calculateVolume(instance);

				handle.execute(channel -> {
					if (volume <= 0.0f) {
						// Pause music unconditionally when volume is zero
						// Other sounds get paused by vanilla if the game is also paused, else we cancel them
						if (isMusic) {
							channel.pause();
						} else if (!dynamic_fps$minecraft.isPaused()) {
							channel.stop();
						}
					} else {
						// Resume music if Minecraft is active *and* the previous volume was zero
						// Prevents us from resuming music when the user returns to a paused game
						// Or the game was just paused and the user is focusing to another window
						if (!dynamic_fps$minecraft.isPaused() && isMusic && before.volumeMultiplier(source) == 0.0f) {
							channel.unpause();
						}
						channel.setVolume(volume);
					}
				});
			}
		});
	}

	/**
	 * Cancels playing sounds while we are overwriting the volume to be off.
	 *
	 * This is done in favor of actually setting the volume to zero because it
	 * Allows pausing and resuming the sound engine without cancelling all active sounds.
	 */
	@Inject(method = { "play", "playDelayed" }, at = @At("HEAD"), cancellable = true)
	private void play(SoundInstance instance, CallbackInfo callbackInfo) {
		if (DynamicFPSMod.volumeMultiplier(instance.getSource()) == 0.0f) {
			callbackInfo.cancel();
		}
	}

	/**
	 * Applies the user's requested volume multiplier to any newly played sounds.
	 */
	@ModifyReturnValue(method = "getVolume", at = @At("RETURN"))
	private float getVolume(float original, @Local @Nullable SoundSource source) {
		return this.adjustVolume(original, source);
	}

	private float adjustVolume(float value, @Nullable SoundSource source) {
		if (source == null) {
			source = SoundSource.MASTER;
		}

		return value * DynamicFPSMod.volumeMultiplier(source);
	}
}
