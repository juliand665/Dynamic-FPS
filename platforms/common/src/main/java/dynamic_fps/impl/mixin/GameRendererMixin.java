package dynamic_fps.impl.mixin;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	/**
	 * Implements the mod's big feature.
	 * <p>
	 * Note: Inject after the pause on lost focus check,
	 * This allows the feature to work even at zero FPS.
	 */
	@Redirect(
		method = "render",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/Minecraft;noRender:Z",
			opcode = Opcodes.GETFIELD
		)
	)
	private boolean skipRendering(Minecraft instance) {
		return instance.noRender || !DynamicFPSMod.checkForRender();
	}

	/**
	 * Cancels rendering the world if it is determined to currently not be visible.
	 */
	@Inject(
		method = { "renderLevel", "renderItemActivationAnimation" },
		at = @At("HEAD"),
		cancellable = true
	)
	private void shouldRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.shouldShowLevels()) {
			callbackInfo.cancel();
		}
	}
}
