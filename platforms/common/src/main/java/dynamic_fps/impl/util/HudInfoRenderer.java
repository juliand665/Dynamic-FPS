package dynamic_fps.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import static dynamic_fps.impl.util.Localization.localized;

import com.mojang.blaze3d.vertex.PoseStack;

import dynamic_fps.impl.DynamicFPSMod;

public final class HudInfoRenderer {
	private static final Minecraft minecraft = Minecraft.getInstance();

	public static void renderInfo(PoseStack poseStack) {
		if (DynamicFPSMod.disabledByUser()) {
			drawCenteredText(poseStack, localized("gui", "hud.disabled"), 32);
		} else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(poseStack, localized("gui", "hud.reducing"), 32);
		}
	}

	private static void drawCenteredText(PoseStack poseStack, Component component, float y) {
		Font fontRenderer = minecraft.gui.getFont();

		int stringWidth = fontRenderer.width(component);
		int windowWidth = minecraft.getWindow().getGuiScaledWidth();

		fontRenderer.drawShadow(
			poseStack,
			component,
			(windowWidth - stringWidth) / 2f,
			y,
			0xFFFFFFFF
		);
	}
}
