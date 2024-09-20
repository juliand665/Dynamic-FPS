package dynamic_fps.impl.mixin;

import dynamic_fps.impl.PowerState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.Window;

import dynamic_fps.impl.DynamicFPSMod;

@Mixin(Window.class)
public class WindowMixin {
	/**
	 * Sets a frame rate limit while we're cancelling some or all rendering.
	 */
	@Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
	private void onGetFramerateLimit(CallbackInfoReturnable<Integer> callbackInfo) {
		PowerState state = DynamicFPSMod.powerState();

		if (state != PowerState.FOCUSED) {
			// Instruct Minecraft to render a minimum of 15 FPS
			// Going lower here makes resuming again feel sluggish
			callbackInfo.setReturnValue(Math.max(DynamicFPSMod.targetFrameRate(), 15));
		}
	}
}
