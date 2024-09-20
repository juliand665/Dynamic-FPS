package dynamic_fps.impl.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dynamic_fps.impl.config.BatteryTrackerConfig;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.feature.battery.BatteryTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;

import dynamic_fps.impl.DynamicFPSMod;

public final class HudInfoRenderer {
	public static void renderInfo(PoseStack poseStack) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.options.hideGui || minecraft.screen != null) {
			return;
		}

		if (DynamicFPSConfig.INSTANCE.batteryTracker().enabled()) {
			drawBatteryOverlay(poseStack);
		}

		if (DynamicFPSMod.disabledByUser()) {
			drawCenteredText(poseStack, Components.translatable("gui", "hud.disabled"));
		} else if (DynamicFPSMod.isForcingLowFPS()) {
			drawCenteredText(poseStack, Components.translatable("gui", "hud.reducing"));
		}
	}

	private static void drawCenteredText(PoseStack poseStack, Component component) {
		Minecraft minecraft = Minecraft.getInstance();
		int width = minecraft.getWindow().getGuiScaledWidth() / 2;

		GuiComponent.drawCenteredString(poseStack, minecraft.font, component, width, 32, 0xFFFFFF);
	}

	private static void drawBatteryOverlay(PoseStack poseStack) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.options.renderDebug || !BatteryTracker.hasBatteries()) {
			return;
		}

		BatteryTrackerConfig.DisplayConfig config = DynamicFPSConfig.INSTANCE.batteryTracker().display();

		if (!config.condition().isConditionMet()) {
			return;
		}

		int index = BatteryTracker.charge() / 10;
		String type = BatteryUtil.isCharging(BatteryTracker.status()) ? "charging" : "draining";
		ResourceLocation icon = ResourceLocations.of("dynamic_fps", "textures/battery/icon/" + type + "_" + index + ".png");

		// pair of coordinates
		int[] position = config.placement().get(minecraft.getWindow());

		RenderSystem.setShaderTexture(0, icon);
		// resource, x, y, z, ?, ?, width, height, width, height
		GuiComponent.blit(poseStack, position[0], position[1], 0, 0.0f, 0.0f, 16, 16, 16, 16);
		// font, text, x, y, text color
		GuiComponent.drawString(poseStack, minecraft.font, BatteryTracker.charge() + "%", position[0] + 20, position[1] + 4, 0xFFFFFF);
	}
}
