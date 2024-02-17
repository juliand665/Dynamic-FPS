package dynamic_fps.impl.config;

import java.util.HashMap;
import java.util.Map;

import dynamic_fps.impl.GraphicsState;
import dynamic_fps.impl.PowerState;
import net.minecraft.sounds.SoundSource;

public final class Config {
	private int frameRateTarget;
	private final Map<SoundSource, Float> volumeMultipliers;
	private GraphicsState graphicsState;
	private boolean showToasts;
	private boolean runGarbageCollector;

	public static final Config ACTIVE = new Config(-1, new HashMap<>(), GraphicsState.DEFAULT, true, false);

	public Config(int frameRateTarget, Map<SoundSource, Float> volumeMultipliers, GraphicsState graphicsState, boolean showToasts, boolean runGarbageCollector) {
		this.frameRateTarget = frameRateTarget;
		this.volumeMultipliers = new HashMap<>(volumeMultipliers); // Ensure the map is mutable
		this.graphicsState = graphicsState;
		this.showToasts = showToasts;
		this.runGarbageCollector = runGarbageCollector;
	}

	public int frameRateTarget() {
		return this.frameRateTarget;
	}

	public void setFrameRateTarget(int value) {
		this.frameRateTarget = value;
	}

	public float volumeMultiplier(SoundSource source) {
		if (this.rawVolumeMultiplier(SoundSource.MASTER) == 0.0f) {
			return 0.0f;
		} else {
			return this.rawVolumeMultiplier(source);
		}
	}

	public float rawVolumeMultiplier(SoundSource source) {
		return this.volumeMultipliers.getOrDefault(source, 1.0f);
	}

	public void setVolumeMultiplier(SoundSource source, float value) {
		if (value != 1.0f) {
			this.volumeMultipliers.put(source, value);
		} else {
			this.volumeMultipliers.remove(source); // Same as default value
		}
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

	public static Config getDefault(PowerState state) {
		switch (state) {
			case HOVERED: {
				return new Config(60, withMasterVolume(1.0f), GraphicsState.DEFAULT, true, false);
			}
			case UNFOCUSED: {
				return new Config(1, withMasterVolume(0.25f), GraphicsState.DEFAULT, false, false);
			}
			case ABANDONED: {
				return new Config(10, withMasterVolume(1.0f), GraphicsState.DEFAULT, false, false);
			}
			case INVISIBLE: {
				return new Config(0, withMasterVolume(0.0f), GraphicsState.DEFAULT, false, false);
			}
			default: {
				throw new RuntimeException("Getting default configuration for unhandled power state " + state.toString());
			}
		}
	}

	private static Map<SoundSource, Float> withMasterVolume(float value) {
		Map<SoundSource, Float> volumes = new HashMap<>();
		volumes.put(SoundSource.MASTER, value);
		return volumes;
	}
}
