package net.lostluma.dynamic_fps.impl.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dynamic_fps.impl.util.HudInfoRenderer;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeIngameGui.class)
public class GuiMixin {
	/**
	 * Render info on whether Dynamic FPS is disabled or always reducing the user's FPS.
	 */
	@Inject(method = "render", at = @At("HEAD"))
	private void renderSavingIndicator(PoseStack poseStack, float partialTick, CallbackInfo ci) {
		HudInfoRenderer.renderInfo(poseStack);
	}
}
