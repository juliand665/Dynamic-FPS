package dynamic_fps.impl.util;

import dynamic_fps.impl.config.BatteryTrackerConfig;
import dynamic_fps.impl.feature.battery.BatteryTracker;
import net.lostluma.battery.api.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static dynamic_fps.impl.util.Localization.localized;

import net.minecraft.resources.ResourceLocation;

import dynamic_fps.impl.DynamicFPSMod;

public final class HudInfoRenderer {
	private static final Minecraft minecraft = Minecraft.getInstance();

	public static void renderInfo(GuiGraphics guiGraphics) {
		if (DynamicFPSMod.batteryTracking().enabled()) {
			drawBatteryOverlay(guiGraphics);
		}

		if (DynamicFPSMod.disabledByUser()) {
			drawCenteredText(guiGraphics, localized("gui", "hud.disabled"));
		} else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(guiGraphics, localized("gui", "hud.reducing"));
		}
	}

	private static void drawCenteredText(GuiGraphics guiGraphics, Component component) {
		int width = guiGraphics.guiWidth() / 2;

		guiGraphics.drawCenteredString(minecraft.font, component, width, 32, 0xFFFFFF);
	}

	private static void drawBatteryOverlay(GuiGraphics graphics) {
		if (minecraft.screen != null || minecraft.getDebugOverlay().showDebugScreen() || !BatteryTracker.isAvailable()) {
			return;
		}

		BatteryTrackerConfig.DisplayConfig config = DynamicFPSMod.batteryTracking().display();

		if (!config.condition().isConditionMet()) {
			return;
		}

		int index = BatteryTracker.charge() / 10;
		String type = BatteryTracker.status() == State.CHARGING ? "charging" : "draining";
		ResourceLocation icon = ResourceLocations.of("dynamic_fps", "textures/battery/icon/" + type + "_" + index + ".png");

		// pair of coordinates
		int[] position = config.placement().get(graphics);

		// resource, x, y, z, ?, ?, width, height, width, height
		graphics.blit(icon, position[0], position[1], 0, 0.0f, 0.0f, 16, 16, 16, 16);
		// font, text, x, y, text color
		graphics.drawString(minecraft.font, BatteryTracker.charge() + "%", position[0] + 20, position[1] + 4, 0xFFFFFF);
	}
}
