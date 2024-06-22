package dynamic_fps.impl.feature.battery;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.Logging;
import net.lostluma.battery.api.Battery;
import net.lostluma.battery.api.Manager;
import net.lostluma.battery.api.State;
import net.lostluma.battery.api.exception.LibraryLoadError;
import net.lostluma.battery.api.util.LibraryUtil;
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
		if (manager != null || !DynamicFPSConfig.INSTANCE.batteryTracker().enabled()) {
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
			Thread.ofVirtual().name("refresh-battery").start(BatteryTracker::updateBatteries);
		}
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
			aggregate += battery.stateOfCharge();
			newStatus = mergeStates(newStatus, battery.state());
		}

		int newCharge = Math.round(aggregate / batteries.size());

		if (readInitialData && charge != newCharge) {
			changed = true;
			DynamicFPSMod.onBatteryChargeChanged(charge, newCharge);
		}

		if (readInitialData && status != newStatus) {
			DynamicFPSMod.onBatteryStatusChanged(status ,newStatus);
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
		LibraryUtil.setCacheDir(Platform.getInstance().getCacheDir());
		LibraryUtil.setAllowDownloads(DynamicFPSConfig.INSTANCE.downloadNatives());
	}

	private static Manager createManager() {
		Manager result = null;

		try {
			result = Manager.create();
		} catch (IOException e) {
			Logging.getLogger().warn("Failed to create battery manager!", e);
		} catch (LibraryLoadError e) {
			// No native backend library is available for this OS or platform
			Logging.getLogger().warn("Battery tracker feature unavailable!");
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
