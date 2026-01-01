package dynamic_fps.impl.feature.battery;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.Components;
import dynamic_fps.impl.util.Logging;
import dynamic_fps.impl.util.Threads;
import net.lostluma.battery.api.Battery;
import net.lostluma.battery.api.Manager;
import net.lostluma.battery.api.State;
import net.lostluma.battery.api.exception.LibraryLoadError;
import net.lostluma.battery.api.exception.NetworkError;
import net.lostluma.battery.api.util.Library;
import net.lostluma.battery.api.util.Testing;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;

public class BatteryTracker {
	private static boolean readInitialData = false;

	private static volatile int charge = 0;
	private static volatile State status = State.UNKNOWN;

	private static @Nullable Manager manager = null;
	private static Collection<Battery> batteries = Collections.emptyList();

	private static boolean threadStarted = false;
	private static final Duration updateInterval = Duration.of(15, ChronoUnit.SECONDS);

	public static int charge() {
		return charge;
	}

	public static State status() {
		return status;
	}

	public static boolean hasBatteries() {
		return !batteries.isEmpty();
	}

	public static void init() {
		// Close previous manager to free up
		// Any underlying, active resources.
		if (manager != null) {
			manager.close();

			manager = null;
			batteries = Collections.emptyList();
		}

		if (!isFeatureEnabled()) {
			return;
		}

		customizeInstallation();

		Manager temp = createManager();
		batteries = getBatteries(temp);

		if (batteries.isEmpty()) {
			if (temp != null) {
				temp.close();
			}
		} else {
			manager = temp; // Keep around to allow updating batteries

			if (!threadStarted) {
				threadStarted = true;
				Threads.create("refresh-battery", BatteryTracker::updateBatteries);
			}
		}
	}

	public static boolean isFeatureEnabled() {
		return DynamicFPSConfig.INSTANCE.batteryTracker().enabled();
	}

	private static State mergeStates(State a, State b) {
		if (a == b) {
			return a;
		} else if (a == State.CHARGING || b == State.CHARGING) {
			return State.CHARGING;
		} else if (a == State.DISCHARGING || b == State.DISCHARGING) {
			return State.DISCHARGING;
		} else {
			return a == State.UNKNOWN ? b : a;
		}
	}

	private static void updateState() {
		boolean changed = false;

		float aggregate = 0.0f;
		State newStatus = State.UNKNOWN;

		for (Battery battery : batteries) {
			aggregate += battery.stateOfCharge().percent();
			newStatus = mergeStates(newStatus, battery.state());
		}

		int newCharge = Math.round(aggregate / batteries.size());

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
			for (Battery battery : batteries) {
				try {
					battery.update();
				} catch (IOException e) {
					Logging.getLogger().warn("Failed to update battery!", e);
				}
			}

			updateState();

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

	private static Collection<Battery> getBatteries(@Nullable Manager manager) {
		Collection<Battery> result = Collections.emptyList();

		if (manager == null) {
			return result;
		}

		try {
			result = manager.batteries();
		} catch (IOException e) {
			Logging.getLogger().warn("Failed to query system batteries!", e);
		}

		return result;
	}
}
