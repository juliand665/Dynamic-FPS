package dynamicfps.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.SplashScreen;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class DynamicFPSMixin {
	@Shadow
	@Final
	private MinecraftClient client;
	
	/**
	 Implements the mod's big feature.
	 */
	@Inject(at = @At("HEAD"), method = "render(FJZ)V", cancellable = true)
	private void onRender(CallbackInfo callbackInfo) {
		boolean shouldRender = client.isWindowFocused() || !client.options.pauseOnLostFocus;
		if (!shouldRender) {
			try {
				Thread.sleep(30); // forcibly slow down
			} catch (InterruptedException ignored) {}
			callbackInfo.cancel();
		}
	}
	
	/**
	 cancels world rendering under certain conditions
	 */
	@Inject(at = @At("HEAD"), method = "renderWorld(FJ)V", cancellable = true)
	private void onRenderWorld(CallbackInfo callbackInfo) {
		if (client.getOverlay() instanceof SplashScreen) {
			callbackInfo.cancel();
		}
	}
}
