package dynamic_fps.impl.feature.battery;

import dynamic_fps.impl.util.ResourceLocations;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static dynamic_fps.impl.util.Localization.localized;

public class BatteryToast implements Toast {
	private long firstRender;

	private final Component title;
	private Component description;
	private final ResourceLocation icon;

	private static final ResourceLocation MOD_ICON = ResourceLocations.of("dynamic_fps", "textures/battery/toast/background_icon.png");
	private static final ResourceLocation BACKGROUND_IMAGE = ResourceLocations.of("dynamic_fps", "textures/battery/toast/background.png");

	public BatteryToast(Component title, ResourceLocation icon) {
		this.title = title;
		this.icon = icon;
	}

	@Override
	public @NotNull Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long currentTime) {
		if (this.firstRender == 0) {
			this.firstRender = currentTime;
			// Initialize when first rendering so the battery percentage is mostly up-to-date
			this.description = localized("toast", "battery_charge", BatteryTracker.charge());
		}

		// resource, x, y, z, ?, ?, width, height, width, height
		graphics.blit(BACKGROUND_IMAGE, 0, 0, 0, 0.0f, 0.0f, this.width(), this.height(), this.width(), this.height());

		graphics.blit(MOD_ICON, 2, 2, 0, 0.0f, 0.0f, 8, 8, 8, 8);
		graphics.blit(this.icon, 8, 8, 0, 0.0f, 0.0f, 16, 16, 16, 16);

		graphics.drawString(toastComponent.getMinecraft().font, this.title, 30, 7, 0x5f3315, false);
		graphics.drawString(toastComponent.getMinecraft().font, this.description, 30, 18, -16777216, false);

		return currentTime - this.firstRender >= 5000.0 * toastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
	}
}
