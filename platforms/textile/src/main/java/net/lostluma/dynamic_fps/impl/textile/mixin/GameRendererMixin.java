package net.lostluma.dynamic_fps.impl.textile.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.renderer.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@ModifyExpressionValue(
		method = "render",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/Minecraft;noRender:Z",
			opcode = Opcodes.GETFIELD
		)
	)
	private boolean skipRendering(boolean original) {
		return original || !DynamicFPSMod.checkForRender();
	}
}
