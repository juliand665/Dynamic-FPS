package dynamic_fps.impl.feature.battery;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dynamic_fps.impl.util.ResourceLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static dynamic_fps.impl.util.Localization.localized;

public class BatteryToast implements Toast {
	private long firstRender;

	private Component title;
	private Component description;
	private ResourceLocation icon;

	private static BatteryToast queuedToast;

	private static final ResourceLocation MOD_ICON = ResourceLocations.of("dynamic_fps", "textures/battery/toast/background_icon");
	private static final ResourceLocation BACKGROUND_IMAGE = ResourceLocations.of("dynamic_fps", "textures/battery/toast/background");

	private BatteryToast(Component title, ResourceLocation icon) {
		this.title = title;
		this.icon = icon;
	}

	/**
	 * Queue some information to be shown as a toast.
	 * If an older toast of the same type is already queued its information will be replaced.
	 */
	public static void queueToast(Component title, ResourceLocation icon) {
		if (queuedToast != null) {
			queuedToast.title = title;
			queuedToast.icon = icon;
		} else {
			queuedToast = new BatteryToast(title, icon);
			Minecraft.getInstance().getToasts().addToast(queuedToast);
		}
	}

	@Override
	public @NotNull Visibility render(PoseStack poseStack, ToastComponent toastComponent, long currentTime) {
		if (this.firstRender == 0) {
			if (this == queuedToast) {
				queuedToast = null;
			}

			this.firstRender = currentTime;
			// Initialize when first rendering so the battery percentage is mostly up-to-date
			this.description = localized("toast", "battery_charge", BatteryTracker.charge());
		}

		RenderSystem.setShaderTexture(0, BACKGROUND_IMAGE);
		// resource, x, y, z, ?, ?, width, height, width, height
		GuiComponent.blit(poseStack, 0, 0, 0, 0.0f, 0.0f, this.width(), this.height(), this.width(), this.height());

		RenderSystem.setShaderTexture(0, MOD_ICON);
		GuiComponent.blit(poseStack, 2, 2, 0, 0.0f, 0.0f, 8, 8, 8, 8);
		RenderSystem.setShaderTexture(0, this.icon);
		GuiComponent.blit(poseStack, 8, 8, 0, 0.0f, 0.0f, 16, 16, 16, 16);

		GuiComponent.drawString(poseStack, toastComponent.getMinecraft().font, this.title, 30, 7, 0x5f3315);
		GuiComponent.drawString(poseStack, toastComponent.getMinecraft().font, this.description, 30, 18, -16777216);

		return currentTime - this.firstRender >= 5000.0 * toastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
	}
}
