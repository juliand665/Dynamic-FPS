package dynamic_fps.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import static dynamic_fps.impl.util.Localization.localized;

import dynamic_fps.impl.DynamicFPSMod;

public final class HudInfoRenderer {
	private static final Minecraft minecraft = Minecraft.getInstance();

	public static void renderInfo() {
		if (DynamicFPSMod.disabledByUser()) {
			drawCenteredText(localized("gui", "hud.disabled"), 32);
		} else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(localized("gui", "hud.reducing"), 32);
		}
	}

	private static void drawCenteredText(String component, float y) {
		Font fontRenderer = minecraft.gui.getFont();

		int stringWidth = fontRenderer.width(component);
		int windowWidth = minecraft.window.getGuiScaledWidth();

		fontRenderer.drawShadow(
			component,
			(windowWidth - stringWidth) / 2f,
			y,
			0xFFFFFFFF
		);
	}
}
