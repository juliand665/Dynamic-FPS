package dynamic_fps.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.Window;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.feature.state.IdleHandler;
import net.minecraft.client.Minecraft;
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

	@Inject(method = "setScreen", at = @At("HEAD"))
	private void setScreen(CallbackInfo callbackInfo) {
		IdleHandler.onActivity();
	}

	@WrapMethod(method = "renderFrame")
	private void renderFrame(boolean advanceGameTime, Operation<Void> original) {
		if (!DynamicFPSMod.checkForRender()) {
			this.pauseIfInactive();
		} else {
			original.call(advanceGameTime);
		}
	}
}
