package dynamic_fps.impl.config;

import java.util.Map;

import com.google.gson.annotations.SerializedName;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.config.option.IgnoreInitialClick;

public final class DynamicFPSConfig {
	private boolean enabled;
	private boolean uncapMenuFrameRate;
	private IgnoreInitialClick ignoreInitialClick;
	private IdleConfig idle;
	private BatteryTrackerConfig batteryTracker;
	private VolumeTransitionConfig volumeTransitionSpeed;
	private boolean downloadNatives;
	private boolean mockBatteryData;

	@SerializedName("states")
	private Map<PowerState, Config> configs;

	public static final DynamicFPSConfig DEFAULTS;
	public static final DynamicFPSConfig INSTANCE;

	static {
		DEFAULTS = Serialization.loadDefault();
		INSTANCE = Serialization.loadPersonalized();

		for (Map.Entry<PowerState, Config> entry: INSTANCE.configs.entrySet()) {
			entry.getValue().state = entry.getKey();
		}
	}

	public Config get(PowerState state) {
		if (state == PowerState.FOCUSED) {
			return Config.ACTIVE;
		} else {
			return configs.get(state);
		}
	}

	public boolean enabled() {
		return this.enabled;
	}

	public void setEnabled(boolean value) {
		this.enabled = value;
	}

	public IdleConfig idle() {
		return this.idle;
	}

	public BatteryTrackerConfig batteryTracker() {
		return this.batteryTracker;
	}

	public VolumeTransitionConfig volumeTransitionSpeed() {
		return this.volumeTransitionSpeed;
	}

	public boolean uncapMenuFrameRate() {
		return this.uncapMenuFrameRate;
	}

	public void setUncapMenuFrameRate(boolean value) {
		this.uncapMenuFrameRate = value;
	}

	public IgnoreInitialClick ignoreInitialClick() {
		return this.ignoreInitialClick;
	}

	public void setIgnoreInitialClick(IgnoreInitialClick value) {
		this.ignoreInitialClick = value;
	}

	public boolean downloadNatives() {
		return this.downloadNatives;
	}

	public void setDownloadNatives(boolean value) {
		this.downloadNatives = value;
	}

	public boolean mockBatteryData() {
		return this.mockBatteryData;
	}

	public void setMockBatteryData(boolean value) {
		this.mockBatteryData = value;
	}

	public void save() {
		Serialization.save();
	}
}
