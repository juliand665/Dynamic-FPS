package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "runTick", at = @At("HEAD"))
	private void onRunTick(CallbackInfo callbackInfo) {
		DynamicFPSMod.onNewFrame();
	}
}
