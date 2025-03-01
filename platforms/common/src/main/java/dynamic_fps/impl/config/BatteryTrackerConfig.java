package dynamic_fps.impl.config;

import dynamic_fps.impl.config.option.BatteryIndicatorCondition;
import dynamic_fps.impl.config.option.BatteryIndicatorPlacement;

public class BatteryTrackerConfig {
	private boolean enabled;
	private int criticalLevel;
	private boolean switchStates;
	private boolean notifications;
	private DisplayConfig display;

	public boolean enabled() {
		return this.enabled;
	}

	public void setEnabled(boolean value) {
		this.enabled = value;
	}

	public int criticalLevel() {
		return this.criticalLevel;
	}

	public void setCriticalLevel(int value) {
		this.criticalLevel = value;
	}

	public boolean switchStates() {
		return this.switchStates;
	}

	public void setSwitchStates(boolean value) {
		this.switchStates = value;
	}

	public boolean notifications() {
		return this.notifications;
	}

	public void setNotifications(boolean value) {
		this.notifications = value;
	}

	public DisplayConfig display() {
		return this.display;
	}

	public static class DisplayConfig {
		private BatteryIndicatorCondition condition;
		private BatteryIndicatorPlacement placement;

		public BatteryIndicatorCondition condition() {
			return this.condition;
		}

		public void setCondition(BatteryIndicatorCondition value) {
			this.condition = value;
		}

		public BatteryIndicatorPlacement placement() {
			return this.placement;
		}

		public void setPlacement(BatteryIndicatorPlacement value) {
			this.placement = value;
		}

		public boolean isActive() {
			return this.condition != BatteryIndicatorCondition.DISABLED;
		}
	}
}
