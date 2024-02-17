package dynamic_fps.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.network.chat.Component;

import static dynamic_fps.impl.util.Localization.localized;

import org.joml.Matrix4f;

import dynamic_fps.impl.DynamicFPSMod;

public final class HudInfoRenderer {
	private static final Minecraft minecraft = Minecraft.getInstance();

	public static void renderInfo(GuiGraphics guiGraphics) {
		if (DynamicFPSMod.disabledByUser()) {
			drawCenteredText(guiGraphics, localized("gui", "hud.disabled"), 32);
		} else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(guiGraphics, localized("gui", "hud.reducing"), 32);
		}
	}

	private static void drawCenteredText(GuiGraphics guiGraphics, Component component, float y) {
		Font fontRenderer = minecraft.gui.getFont();

		int stringWidth = fontRenderer.width(component);
		int windowWidth = minecraft.getWindow().getGuiScaledWidth();

		fontRenderer.drawInBatch(
			component,
			(windowWidth - stringWidth) / 2f,
			y,
			0xFFFFFFFF,
			true,
			new Matrix4f(),
			guiGraphics.bufferSource(),
			DisplayMode.NORMAL,
			0,
			255
		);
	}
}
