package dynamic_fps.impl.feature.battery;

import dynamic_fps.impl.util.ResourceLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static dynamic_fps.impl.util.Localization.localized;

public class BatteryToast implements Toast {
	private long firstRender;

	private Component title;
	private Component description;
	private ResourceLocation icon;
	private Visibility visibility;

	private static BatteryToast queuedToast;

	private static final ResourceLocation MOD_ICON = ResourceLocations.of("dynamic_fps", "battery/toast/background_icon");
	private static final ResourceLocation BACKGROUND_IMAGE = ResourceLocations.of("dynamic_fps", "battery/toast/background");

	private BatteryToast(Component title, ResourceLocation icon) {
		this.title = title;
		this.icon = icon;
		this.visibility = Visibility.SHOW;
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
			Minecraft.getInstance().getToastManager().addToast(queuedToast);
		}
	}

	@Override
	public @NotNull Visibility getWantedVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager toastManager, long currentTime) {
		if (this.firstRender == 0) {
			return;
		}

		if (currentTime - this.firstRender >= 5000.0 * toastManager.getNotificationDisplayTimeMultiplier()) {
			this.visibility = Visibility.HIDE;
		}
	}

	@Override
	public void render(GuiGraphics graphics, Font font, long currentTime) {
		if (this.firstRender == 0) {
			if (this == queuedToast) {
				queuedToast = null;
			}

			this.firstRender = currentTime;
			// Initialize when first rendering so the battery percentage is mostly up-to-date
			this.description = localized("toast", "battery_charge", BatteryTracker.charge());
		}

		// type, resource, x, y, width, height
		graphics.blitSprite(RenderType::guiTextured, BACKGROUND_IMAGE, 0, 0, this.width(), this.height());

		graphics.blitSprite(RenderType::guiTextured, MOD_ICON, 2, 2, 8, 8);
		graphics.blitSprite(RenderType::guiTextured, this.icon, 8, 8, 16, 16);

		graphics.drawString(Minecraft.getInstance().font, this.title, 30, 7, 0x5f3315, false);
		graphics.drawString(Minecraft.getInstance().font, this.description, 30, 18, -16777216, false);
	}
}
