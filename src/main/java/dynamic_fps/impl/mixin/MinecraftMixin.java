package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Final
    private Window window;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(CallbackInfo callbackInfo) {
		DynamicFPSMod.setWindow(this.window.window);
	}

	/*
	@Inject(method = "setScreen", at = @At("TAIL"))
	private void onSetScreen(CallbackInfo callbackInfo) {
		DynamicFPSMod.onStatusChanged();
	}
	*/
}
