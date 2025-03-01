package dynamic_fps.impl.config.option;

import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.feature.battery.BatteryTracker;
import net.lostluma.battery.api.State;

/**
 * Condition under which the battery indicator HUD is shown.
 */
public enum BatteryIndicatorCondition {
	/**
	 * Never show the battery indicator.
	 */
	DISABLED((() -> false)),

	/**
	 * Show battery indicator when the battery is being drained.
	 */
	DRAINING(() -> BatteryTracker.status() == State.DISCHARGING),

	/**
	 * Show battery indicator when the battery is at a critical level.
	 */
	CRITICAL(() -> {
		int critical = DynamicFPSConfig.INSTANCE.batteryTracker().criticalLevel();
		return DRAINING.isConditionMet() && BatteryTracker.charge() <= critical;
	}),

	/**
	 * Show battery indicator at all times.
	 */
	CONSTANT(() -> true);

	private final Condition condition;

	BatteryIndicatorCondition(Condition condition) {
		this.condition = condition;
	}

	public boolean isConditionMet() {
		return this.condition.isMet();
	}

	@FunctionalInterface
	private interface Condition {
		boolean isMet();
	}
}
