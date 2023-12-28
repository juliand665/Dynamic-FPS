package dynamic_fps.impl.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	/**
	 * Implements the mod's big feature.
	 *
	 * Note: Inject after the pause on lost focus check,
	 * This allows the feature to work even at zero FPS.
	 */
	@ModifyExpressionValue(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;noRender:Z", opcode = Opcodes.GETFIELD))
	private boolean skipRendering(boolean original) {
		return original || !DynamicFPSMod.checkForRender();
	}

	/**
	 * Cancels rendering the world if a it is determined to currently not be visible.
	 */
	@Inject(at = @At("HEAD"), method = { "renderLevel", "renderItemActivationAnimation" }, cancellable = true)
	private void shouldRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.shouldShowLevels()) {
			callbackInfo.cancel();
		}
	}
}
