package dynamic_fps.impl.util;

import dynamic_fps.impl.config.BatteryTrackerConfig;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.feature.battery.BatteryTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;

import dynamic_fps.impl.DynamicFPSMod;

public final class HudInfoRenderer {
	public static void renderInfo(GuiGraphics guiGraphics) {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.options.hideGui || minecraft.screen != null) {
			return;
		}

		if (DynamicFPSConfig.INSTANCE.batteryTracker().enabled()) {
			drawBatteryOverlay(guiGraphics);
		}

		if (DynamicFPSMod.disabledByUser()) {
			drawCenteredText(guiGraphics, Components.translatable("gui", "hud.disabled"));
		} else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(guiGraphics, Components.translatable("gui", "hud.reducing"));
		}
	}

	private static void drawCenteredText(GuiGraphics guiGraphics, Component component) {
		int width = guiGraphics.guiWidth() / 2;
		Minecraft minecraft = Minecraft.getInstance();

		guiGraphics.drawCenteredString(minecraft.font, component, width, 32, -1);
	}

	private static void drawBatteryOverlay(GuiGraphics graphics) {
		Minecraft minecraft = Minecraft.getInstance();
		BatteryTrackerConfig config = DynamicFPSConfig.INSTANCE.batteryTracker();

		if ((!config.showWhenDebug() && minecraft.debugEntries.isF3Visible()) || !BatteryTracker.hasBatteries()) {
			return;
		}

		if (!config.display().condition().isConditionMet()) {
			return;
		}

		int index = BatteryTracker.charge() / 10;
		String type = BatteryUtil.isCharging(BatteryTracker.status()) ? "charging" : "draining";
		ResourceLocation icon = ResourceLocations.of("dynamic_fps", "textures/battery/icon/" + type + "_" + index + ".png");

		// pair of coordinates
		int[] position = config.display().placement().get(minecraft.getWindow());

		// resource, x, y, z, ?, ?, width, height, width, height
		graphics.blit(RenderPipelines.GUI_TEXTURED, icon, position[0], position[1], 0.0f, 0, 16, 16, 16, 16);
		// font, text, x, y, text color
		graphics.drawString(minecraft.font, BatteryTracker.charge() + "%", position[0] + 20, position[1] + 4, -1);
	}
}
