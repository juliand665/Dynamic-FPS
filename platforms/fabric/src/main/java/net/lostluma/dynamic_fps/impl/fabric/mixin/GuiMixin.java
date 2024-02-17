package net.lostluma.dynamic_fps.impl.fabric.mixin;

import dynamic_fps.impl.util.HudInfoRenderer;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	/**
	 * Render info on whether Dynamic FPS is disabled or always reducing the user's FPS.
	 */
	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/SubtitleOverlay;render()V"
		)
	)
	private void render(CallbackInfo callbackInfo) {
		HudInfoRenderer.renderInfo();
	}
}
