package dynamic_fps.impl;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.network.chat.Component;

import static dynamic_fps.impl.util.Localization.localized;

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

	private void drawCenteredText(GuiGraphics drawContext, Component component, float y) {
		Minecraft client = Minecraft.getInstance();
		Font fontRenderer = client.gui.getFont();

		int windowWidth = client.getWindow().getGuiScaledWidth();
		int stringWidth = fontRenderer.width(component);

		fontRenderer.drawInBatch(
			component,
			(windowWidth - stringWidth) / 2f,
			y,
			0xFFFFFFFF,
			true,
			new Matrix4f(),
			drawContext.bufferSource(),
			DisplayMode.NORMAL,
			0,
			255
		);
	}
}
