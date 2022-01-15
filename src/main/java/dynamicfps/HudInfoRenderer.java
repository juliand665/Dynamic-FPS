package dynamicfps;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import static dynamicfps.util.Localization.localized;

public final class HudInfoRenderer implements HudRenderCallback {
	@Override
	public void onHudRender(MatrixStack matrices, float delta) {
		if (DynamicFPSMod.isDisabled()) {
			drawCenteredText(matrices, localized("gui", "hud.disabled"), 32);
		} else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(matrices, localized("gui", "hud.reducing"), 32);
		}
	}
	
	private void drawCenteredText(MatrixStack matrices, Text text, float y) {
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer textRenderer = client.inGameHud.getTextRenderer();
		
		int windowWidth = client.getWindow().getScaledWidth();
		int stringWidth = textRenderer.getWidth(text);
		textRenderer.drawWithShadow(
			matrices,
			text,
			(windowWidth - stringWidth) / 2f, y,
			0xffffff
		);
	}
}
