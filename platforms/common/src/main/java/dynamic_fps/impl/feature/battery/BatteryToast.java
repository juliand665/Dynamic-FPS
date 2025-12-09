package dynamic_fps.impl.feature.battery;

import dynamic_fps.impl.util.Components;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BatteryToast extends BaseToast {
	private static BatteryToast queuedToast;

	private BatteryToast(Component title, Identifier icon) {
		super(title, Component.empty(), icon);
	}

	/**
	 * Queue some information to be shown as a toast.
	 * If an older toast of the same type is already queued its information will be replaced.
	 */
	public static void queueToast(Component title, Identifier icon) {
		if (queuedToast != null) {
			queuedToast.title = title;
			queuedToast.icon = icon;
		} else {
			queuedToast = new BatteryToast(title, icon);
			Minecraft.getInstance().getToastManager().addToast(queuedToast);
		}
	}

	@Override
	public void onFirstRender() {
		if (this == queuedToast) {
			queuedToast = null;
		}

		// Initialize when first rendering so the battery percentage is mostly up-to-date
		this.description = Components.translatable("toast", "battery_charge", BatteryTracker.charge());
	}
}
