package dynamic_fps.impl.mixin;

import dynamic_fps.impl.util.HudInfoRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class GuiMixin {
	/**
	 * Cancels rendering the GUI if it is determined to currently not be visible.
	 */
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void shouldRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.shouldShowLevels()) {
			callbackInfo.cancel();
		}
	}

	/**
	 * Render info on whether Dynamic FPS is disabled or always reducing the user's FPS.
	 */
	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Gui;renderSavingIndicator(Lnet/minecraft/client/gui/GuiGraphics;)V"
		)
	)
	private void render(GuiGraphics guiGraphics, float f, CallbackInfo callbackInfo) {
		HudInfoRenderer.renderInfo(guiGraphics);
	}
}
