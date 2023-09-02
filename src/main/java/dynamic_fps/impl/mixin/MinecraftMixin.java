package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "runTick", at = @At("HEAD"))
	private void onRunTick(CallbackInfo callbackInfo) {
		DynamicFPSMod.onNewFrame();
	}

	/*
	 * Sets a frame rate limit while it is lowered synthetically or a menu-type screen is open.
	 */
	@Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
	private void onGetFramerateLimit(CallbackInfoReturnable<Integer> callbackInfo) {
		if (DynamicFPSMod.shouldReduceFramerate()) {
			// Keep the existing frame rate limit if the user has set the game to run at eg. 30 FPS
			callbackInfo.setReturnValue(Math.min(callbackInfo.getReturnValue(), DynamicFPSMod.MENU_FRAMERATE_LIMIT));
		}
	}
}
