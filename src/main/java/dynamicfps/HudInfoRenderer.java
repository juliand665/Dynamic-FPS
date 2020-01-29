package dynamicfps;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class HudInfoRenderer implements HudRenderCallback {
	@Override
	public void onHudRender(float delta) {
		if (DynamicFPSMod.isForcingLowFPS()) {
			MinecraftClient client = MinecraftClient.getInstance();
			TextRenderer textRenderer = client.inGameHud.getFontRenderer();
			
			String textToRender = text("reducing").asFormattedString();
			int windowWidth = client.getWindow().getScaledWidth();
			int stringWidth = textRenderer.getStringWidth(textToRender);
			textRenderer.drawWithShadow(
				textToRender,
				(windowWidth - stringWidth) / 2f, 32,
				0xffffff
			);
		}
	}
	
	private static Text text(String path, Object... args) {
		return new TranslatableText("gui." + DynamicFPSMod.MOD_ID + ".hud." + path, args);
	}
}
