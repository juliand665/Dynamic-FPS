package dynamic_fps.impl.util;

import net.lostluma.battery.api.State;

public class BatteryUtil {
	/**
	 * @return whether the state is charging or full.
	 */
	public static boolean isCharging(State state) {
		// Some devices seem to like frantically changing from
		// Charging to full, even when the battery is not full
		// Prevents frequent HUD changes and notification spam
		return state == State.CHARGING || state == State.FULL;
	}
}
