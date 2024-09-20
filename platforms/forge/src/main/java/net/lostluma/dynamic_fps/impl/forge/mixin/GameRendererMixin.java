package net.lostluma.dynamic_fps.impl.forge.mixin;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private long lastActiveTime;

	// Duplicate of common mixin w/o mixinextras and injected earlier
	// Since the original just does not activate at all on Forge ...?
	// We have to add the pause on lost focus feature ourselves sadly
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void skipRendering(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.checkForRender()) {
			callbackInfo.cancel();

			// Copy of vanilla code
			if (this.minecraft.isWindowActive() || !this.minecraft.options.pauseOnLostFocus || this.minecraft.options.touchscreen && this.minecraft.mouseHandler.isRightPressed()) {
				this.lastActiveTime = Util.getMillis();
			} else if (Util.getMillis() - this.lastActiveTime > 500L) {
				this.minecraft.pauseGame(false);
			}
		}
	}
}
