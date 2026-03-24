package dynamic_fps.impl.mixin;

import dynamic_fps.impl.util.HudInfoRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "extractCrosshair", at = @At("RETURN"))
	private void extractCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
		HudInfoRenderer.renderInfo(graphics);
	}
}
