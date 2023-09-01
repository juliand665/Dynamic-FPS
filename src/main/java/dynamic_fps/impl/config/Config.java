package dynamic_fps.impl.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dynamic_fps.impl.GraphicsState;

public final class Config {
	private int frameRateTarget;
	private float volumeMultiplier;
	private GraphicsState graphicsState;
	private boolean showToasts;
	private boolean runGarbageCollector;

	public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("frame_rate_target").forGetter(Config::frameRateTarget),
		Codec.FLOAT.fieldOf("volume_multiplier").forGetter(Config::volumeMultiplier),
		GraphicsState.CODEC.fieldOf("graphics_state").forGetter(Config::graphicsState),
		Codec.BOOL.fieldOf("show_toasts").forGetter(Config::showToasts),
		Codec.BOOL.fieldOf("run_garbage_collector").forGetter(Config::runGarbageCollector)
	).apply(instance, Config::new));

	public static final Config ACTIVE = new Config(-1, 1.0f, GraphicsState.DEFAULT, true, false);
	public static final Config SUSPENDED = new Config(60, 1.0f, GraphicsState.DEFAULT, true, false);

	public Config(int frameRateTarget, float volumeMultiplier, GraphicsState graphicsState, boolean showToasts, boolean runGarbageCollector) {
		this.frameRateTarget = frameRateTarget;
		this.volumeMultiplier = volumeMultiplier;
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

	public float volumeMultiplier() {
		return this.volumeMultiplier;
	}

	public void setVolumeMultiplier(float value) {
		this.volumeMultiplier = value;
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
