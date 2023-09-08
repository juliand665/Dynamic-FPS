package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.Window;

import dynamic_fps.impl.DynamicFPSMod;

@Mixin(Window.class)
public class WindowMixin {
	@Shadow
	@Final
	private long window;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void postinit(CallbackInfo callbackInfo) {
		DynamicFPSMod.setWindow(this.window);
	}

	/**
	 * Sets a frame rate limit while we're cancelling some or all rendering.
	 */
	@Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
	private void onGetFramerateLimit(CallbackInfoReturnable<Integer> callbackInfo) {
		int target = DynamicFPSMod.targetFrameRate();

		if (target != -1) {
			// We're currently reducing the frame rate
			// Instruct Minecraft to render max 15 FPS
			// Going lower here makes resuming feel sluggish
			callbackInfo.setReturnValue(Math.max(target, 15));
		}
	}
}
