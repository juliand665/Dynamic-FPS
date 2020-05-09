package dynamicfps;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;

public final class HudInfoRenderer implements HudRenderCallback {
	@Override
	public void onHudRender(MatrixStack matrixStack, float delta) {
		if (DynamicFPSMod.isForcingLowFPS()) {
			MinecraftClient client = MinecraftClient.getInstance();
			TextRenderer textRenderer = client.inGameHud.getFontRenderer();
			
			String textToRender = I18n.translate("gui." + DynamicFPSMod.MOD_ID + ".hud.reducing");
			int windowWidth = client.getWindow().getScaledWidth();
			int stringWidth = textRenderer.getWidth(textToRender);
			textRenderer.drawWithShadow(
				matrixStack,
				textToRender,
				(windowWidth - stringWidth) / 2f, 32,
				0xffffff
			);
		}
	}
}
