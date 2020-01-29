package dynamicfps.mixin;

import dynamicfps.DynamicFPSMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	private MinecraftClient client;
	
	/**
	 Implements the mod's big feature.
	 */
	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void onRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.checkForRender()) {
			callbackInfo.cancel();
		}
	}
	
	/**
	 cancels world rendering under certain conditions
	 */
	@Inject(at = @At("HEAD"), method = "renderWorld", cancellable = true)
	private void onRenderWorld(CallbackInfo callbackInfo) {
		if (client.getOverlay() instanceof SplashScreen) {
			callbackInfo.cancel();
		}
	}
}
