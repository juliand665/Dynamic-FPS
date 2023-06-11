package dynamicfps;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.text.Text;

import static dynamicfps.util.Localization.localized;

import org.joml.Matrix4f;

public final class HudInfoRenderer implements HudRenderCallback {
	@Override
	public void onHudRender(GuiGraphics drawContext, float tickDelta) {
		if (DynamicFPSMod.isDisabled()) {
			drawCenteredText(drawContext, localized("gui", "hud.disabled"), 32);
		} else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(drawContext, localized("gui", "hud.reducing"), 32);
		}
	}

	private void drawCenteredText(GuiGraphics drawContext, Text text, float y) {
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer textRenderer = client.inGameHud.getTextRenderer();

		int windowWidth = client.getWindow().getScaledWidth();
		int stringWidth = textRenderer.getWidth(text);

		textRenderer.draw(
			text,
			(windowWidth - stringWidth) / 2f,
			y,
			0xFFFFFFFF,
			true,
			new Matrix4f(),
			drawContext.getVertexConsumers(),
			TextLayerType.NORMAL,
			0,
			255
		);
	}
}
