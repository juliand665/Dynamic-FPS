package dynamic_fps.impl.mixin;

import com.mojang.blaze3d.platform.Window;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.PowerState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Final
    private Window window;

	@Shadow
	@Final
	public Options options;

	// Minecraft considers limits >=260 as infinite
	private static final int NO_FRAME_RATE_LIMIT = 260;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(CallbackInfo callbackInfo) {
		DynamicFPSMod.setWindow(this.window.window);
	}

	/**
	 * Conditionally bypasses the main menu frame rate limit.
	 *
	 * This is done in two cases:
	 * - The window is active, and the user wants to uncap the frame rate
	 * - The window is inactive, and the current FPS limit should be lower
	 */
	@Inject(method = "getFramerateLimit", at = @At(value = "CONSTANT", args = "intValue=60"), cancellable = true)
	private void getFramerateLimit(CallbackInfoReturnable<Integer> callbackInfo) {
		int limit = this.window.getFramerateLimit();

		if (DynamicFPSMod.powerState() != PowerState.FOCUSED) {
			// Limit may be 260 (uncapped)
			if (limit < 60) {
				callbackInfo.setReturnValue(limit);
			}
		} else if (DynamicFPSMod.uncapMenuFrameRate()) {
			if (this.options.enableVsync) {
				// VSync will regulate to a non-infinite value
				callbackInfo.setReturnValue(NO_FRAME_RATE_LIMIT);
			} else {
				// Even though the option "uncaps" the frame rate the max is 250 FPS
				// Since otherwise this will just cause coil whine for no real benefit.
				callbackInfo.setReturnValue(Math.min(limit, NO_FRAME_RATE_LIMIT - 10));
			}
		}
	}
}
