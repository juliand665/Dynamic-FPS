package dynamicfps.mixin;

import dynamicfps.DynamicFPSMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	/**
	 Implements the mod's big feature.
	 */
	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void onRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.checkForRender()) {
			callbackInfo.cancel();
		}
	}

	/*
	 * Cancels rendering the world if a screen is covering the whole window or a splash overlay is present.
	 */
	@SuppressWarnings("squid:S1871") // Multiple conditions, same code
	@Inject(at = @At("HEAD"), method = "renderLevel", cancellable = true)
	private void onRenderLevel(CallbackInfo callbackInfo) {
		if (minecraft.screen != null && minecraft.screen.dynamicfps$rendersBackground()) {
			callbackInfo.cancel();
		} else if (minecraft.getOverlay() instanceof LoadingOverlay splashScreen && splashScreen.dynamicfps$isReloadComplete()) {
			callbackInfo.cancel();
		}
	}
}
