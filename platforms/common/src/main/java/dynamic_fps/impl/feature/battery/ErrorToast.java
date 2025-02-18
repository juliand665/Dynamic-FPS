package dynamic_fps.impl.feature.battery;

import dynamic_fps.impl.util.Components;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ErrorToast extends BaseToast {
	private static final Component TITLE = Components.translatable("toast", "error");

	private ErrorToast(Component description) {
		super(TITLE, description, null);
	}

	/**
	 * Queue some information to be shown as a toast.
	 */
	public static void queueToast(Component description) {
		ErrorToast toast = new ErrorToast(description);
		Minecraft.getInstance().getToastManager().addToast(toast);
	}
}
