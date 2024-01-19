package dynamic_fps.impl.config;

import java.util.EnumMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import dynamic_fps.impl.PowerState;

public final class DynamicFPSConfig {
	private int idleTime; // Seconds
	@SerializedName("states")
	private final Map<PowerState, Config> configs;

	DynamicFPSConfig(int abandonTime, Map<PowerState, Config> configs) {
		this.idleTime = abandonTime;
		this.configs = new EnumMap<>(configs);

		for (var state : PowerState.values()) {
			if (state.configurable) {
				this.configs.computeIfAbsent(state, Config::getDefault);
			}
		}
	}

	public Config get(PowerState state) {
		if (state == PowerState.FOCUSED) {
			return Config.ACTIVE;
		} else {
			return configs.get(state);
		}
	}

	public int idleTime() {
		return this.idleTime;
	}

	public void setIdleTime(int value) {
		this.idleTime = value;
	}

	private Map<PowerState, Config> configs() {
		return this.configs;
	}

	public static DynamicFPSConfig load() {
		return Serialization.load();
	}

	public void save() {
		Serialization.save(this);
	}
}
