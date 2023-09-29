package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.sounds.SoundEngine;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
	/**
	 * Cancels playing sounds while we are overwriting the volume to be off.
	 *
	 * This is done in favor of actually setting the volume to zero, because it
	 * Allows pausing and resuming the sound engine without cancelling active sounds.
	 */
	@Inject(method = { "play", "playDelayed" }, at = @At("HEAD"), cancellable = true)
	private void play(CallbackInfo callbackInfo) {
		if (DynamicFPSMod.volumeMultiplier() == 0.0f) {
			callbackInfo.cancel();
		}
	}
}
