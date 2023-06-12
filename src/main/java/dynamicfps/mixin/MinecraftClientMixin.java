package dynamicfps.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public Screen currentScreen;

	// Default framerate limit on main menu etc.
	private static final int MENU_FRAMERATE_LIMIT = 60;

	/*
	 * Sets a framerate limit when a screen is covering the full window.
	 */
	@Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
	private void onGetFramerateLimit(CallbackInfoReturnable<Integer> callbackInfo) {
		if (this.currentScreen != null && this.currentScreen.dynamicfps$rendersBackground()) {
			// Keep the existing framerate limit if the user has set the game to run at eg. 30 FPS
			callbackInfo.setReturnValue(Math.min(callbackInfo.getReturnValue(), MENU_FRAMERATE_LIMIT));
		}
	}
}
