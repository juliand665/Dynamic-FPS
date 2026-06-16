package dynamic_fps.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import dynamic_fps.impl.Constants;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.PowerState;
import net.minecraft.client.FramerateLimiter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Final
    private Window window;

	@Shadow
	@Final
	public Options options;

	@Shadow
	protected abstract void pauseIfInactive();

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(CallbackInfo callbackInfo) {
		DynamicFPSMod.init();
		DynamicFPSMod.setWindow(this.window.handle());
	}

	@WrapMethod(method = "renderFrame")
	private void renderFrame(boolean advanceGameTime, Operation<Void> original) {
		if (!DynamicFPSMod.checkForRender()) {
			this.pauseIfInactive();
			FramerateLimiter.limitDisplayFPS(Constants.MIN_FRAME_RATE_LIMIT);
		} else {
			original.call(advanceGameTime);
		}
	}

	/**
	 * Apply overwritten vsync preference.
	 */
	@WrapOperation(
		method = "renderFrame",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;",
			ordinal = 0
		)
	)
	private Object renderFrame(OptionInstance<?> instance, Operation<?> original) {
		if (DynamicFPSMod.powerState() == PowerState.FOCUSED) {
			return original.call(instance);
		} else {
			return DynamicFPSMod.enableVsync();
		}
	}
}
