package dynamic_fps.impl.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.config.option.GraphicsState;
import net.minecraft.sounds.SoundSource;

public class Config {
	private int frameRateTarget;
	private final Map<String, Float> volumeMultipliers;
	private GraphicsState graphicsState;
	private boolean showToasts;
	private boolean runGarbageCollector;

	protected transient PowerState state; // Set by main config, allows retrieving values from the default power state config

	public static final Config ACTIVE = new Config(-1, new HashMap<>(), GraphicsState.DEFAULT, true, false);

	public Config(int frameRateTarget, Map<String, Float> volumeMultipliers, GraphicsState graphicsState, boolean showToasts, boolean runGarbageCollector) {
		this.frameRateTarget = frameRateTarget;
		this.volumeMultipliers = new HashMap<>(volumeMultipliers); // Ensure the map is mutable
		this.graphicsState = graphicsState;
		this.showToasts = showToasts;
		this.runGarbageCollector = runGarbageCollector;
	}

	public int frameRateTarget() {
		if (this.frameRateTarget != -1) {
			return this.frameRateTarget;
		} else {
			return Constants.NO_FRAME_RATE_LIMIT;
		}
	}

	public void setFrameRateTarget(int value) {
		if (value == Constants.NO_FRAME_RATE_LIMIT) {
			this.frameRateTarget = -1;
		} else {
			this.frameRateTarget = value;
		}
	}

	public float volumeMultiplier(SoundSource source) {
		if (this.rawVolumeMultiplier(SoundSource.MASTER) == 0.0f) {
			return 0.0f;
		} else {
			return this.rawVolumeMultiplier(source);
		}
	}

	public float rawVolumeMultiplier(SoundSource source) {
		String key = soundSourceName(source);
		return this.volumeMultipliers.getOrDefault(key, 1.0f);
	}

	public void setVolumeMultiplier(SoundSource source, float value) {
		String key = soundSourceName(source);
		Config defaultConfig = DynamicFPSConfig.DEFAULTS.get(this.state);

		if (value != 1.0f || defaultConfig.rawVolumeMultiplier(source) != 1.0f) {
			this.volumeMultipliers.put(key, value);
		} else {
			this.volumeMultipliers.remove(key); // Same as default value
		}
	}

	private static String soundSourceName(SoundSource source) {
		return source.getName().toLowerCase(Locale.ROOT);
	}

	public GraphicsState graphicsState() {
		return this.graphicsState;
	}

	public void setGraphicsState(GraphicsState value) {
		this.graphicsState = value;
	}

	public boolean showToasts() {
		return this.showToasts;
	}

	public void setShowToasts(boolean value) {
		this.showToasts = value;
	}

	public boolean runGarbageCollector() {
		return this.runGarbageCollector;
	}

	public void setRunGarbageCollector(boolean value) {
		this.runGarbageCollector = value;
	}
}
