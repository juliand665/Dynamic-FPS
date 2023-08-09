package dynamicfps.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Nullable
	public Screen screen;

	// Default framerate limit on main menu etc.
	private static final int MENU_FRAMERATE_LIMIT = 60;

	/*
	 * Sets a framerate limit when a screen is covering the full window.
	 */
	@Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
	private void onGetFramerateLimit(CallbackInfoReturnable<Integer> callbackInfo) {
		if (this.screen != null && this.screen.dynamicfps$rendersBackground()) {
			// Keep the existing framerate limit if the user has set the game to run at eg. 30 FPS
			callbackInfo.setReturnValue(Math.min(callbackInfo.getReturnValue(), MENU_FRAMERATE_LIMIT));
		}
	}
}