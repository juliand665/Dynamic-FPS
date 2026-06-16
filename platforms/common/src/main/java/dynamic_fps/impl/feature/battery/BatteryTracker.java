package dynamic_fps.impl.feature.battery;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.Components;
import dynamic_fps.impl.util.Logging;
import dynamic_fps.impl.util.Threads;
import net.lostluma.battery.api.Manager;
import net.lostluma.battery.api.MultiBatteryView;
import net.lostluma.battery.api.State;
import net.lostluma.battery.api.exception.LibraryLoadError;
import net.lostluma.battery.api.exception.NetworkError;
import net.lostluma.battery.api.util.Library;
import net.lostluma.battery.api.util.Testing;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class BatteryTracker {
	private static boolean readInitialData = false;

	private static volatile int charge = 0;
	private static volatile State status = State.UNKNOWN;

	private static @Nullable Manager manager = null;
	private static @Nullable MultiBatteryView view = null;

	private static @Nullable Thread updateThread = null;
	private static final Duration updateInterval = Duration.of(15, ChronoUnit.SECONDS);

	public static int charge() {
		return charge;
	}

	public static State status() {
		return status;
	}

	public static boolean hasBatteries() {
		return view != null;
	}

	public static void init() {
		// Close previous manager to free up
		// Any underlying, active resources.
		if (manager != null) {
			manager.close();

			view = null;
			manager = null;
		}

		if (!isFeatureEnabled()) {
			return;
		}

		customizeInstallation();

		Manager temp = createManager();
		view = getSystemBatteryView(temp);

		if (view == null) {
			if (temp != null) {
				temp.close();
			}
		} else {
			manager = temp; // Keep around to allow updating batteries

			if (updateThread == null) {
				updateThread = Threads.create("refresh-battery", BatteryTracker::updateBatteries);
			}
		}
	}

	public static void close() {
		if (updateThread != null) {
			updateThread.interrupt();
		}
	}

	public static boolean isFeatureEnabled() {
		return DynamicFPSConfig.INSTANCE.batteryTracker().enabled();
	}

	private static void updateState() {
		if (view == null) {
			throw new RuntimeException("view is unset");
		}

		boolean changed = false;

		State newStatus = view.state();
		int newCharge = Math.round(view.stateOfCharge().percent());

		if (readInitialData && charge != newCharge) {
			changed = true;

			int current = charge;
			Threads.runOnMainThread(() -> DynamicFPSMod.onBatteryChargeChanged(current, newCharge));
		}

		if (readInitialData && status != newStatus) {
			changed = true;

			State current = status;
			State updated = newStatus;
			Threads.runOnMainThread(() -> DynamicFPSMod.onBatteryStatusChanged(current, updated));
		}

		charge = newCharge;
		status = newStatus;

		if (!readInitialData || changed) {
			readInitialData = true;
			// Unplugged state may have toggled
			DynamicFPSMod.onStatusChanged(false);
		}
	}

	private static void updateBatteries() {
		boolean active = true;

		while (active) {
			if (view != null) {
				try {
					view.update();
				} catch (IOException e) {
					Logging.getLogger().warn("Failed to update batteries!", e);
				}

				updateState();
			}

			try {
				Thread.sleep(updateInterval);
			} catch (InterruptedException e) {
				active = false;
				Thread.currentThread().interrupt();
			}
		}

		if (manager != null) {
			manager.close();
		}
	}

	private static void customizeInstallation() {
		Library.setCacheDir(Platform.getInstance().getCacheDir());
		Library.setAllowDownloads(DynamicFPSConfig.INSTANCE.downloadNatives());
	}

	private static Manager createManager() {
		Manager result = null;

		try {
			if (!DynamicFPSConfig.INSTANCE.mockBatteryData()) {
				result = Manager.create();
			} else {
				result = Testing.mockManager();
			}
		} catch (IOException e) {
			Logging.getLogger().warn("Failed to create battery manager!", e);
		} catch (LibraryLoadError e) {
			// No native backend library is available for this OS or platform
			Logging.getLogger().warn("Battery tracker feature unavailable!");

			String path;

			if (e instanceof NetworkError) {
				path = "http_error";
			} else if (DynamicFPSConfig.INSTANCE.downloadNatives()) {
				path = "no_support";
			} else {
				path = "no_library";
			}

			Threads.runOnMainThread(() -> ErrorToast.queueToast(Components.translatable("toast", path)));
		}

		return result;
	}

	private static @Nullable MultiBatteryView getSystemBatteryView(@Nullable Manager manager) {
		if (manager == null) {
			return null;
		}

		MultiBatteryView result = null;

		try {
			result = manager.view().orElse(null);
		} catch (IOException e) {
			Logging.getLogger().warn("Failed to query system batteries!", e);
		}

		return result;
	}
}
