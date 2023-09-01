package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	/**
	 * Implements the mod's big feature.
	 */
	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void onRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.checkForRender()) {
			callbackInfo.cancel();
		}
	}

	/*
	 * Cancels rendering the world if a it is determined to currently not be visible.
	 */
	@Inject(at = @At("HEAD"), method = { "renderLevel", "renderItemActivationAnimation" }, cancellable = true)
	private void shouldRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.shouldShowLevels()) {
			callbackInfo.cancel();
		}
	}
}
