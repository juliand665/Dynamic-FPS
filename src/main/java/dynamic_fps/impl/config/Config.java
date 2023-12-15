package dynamic_fps.impl.config;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dynamic_fps.impl.GraphicsState;
import dynamic_fps.impl.util.EnumCodec;
import net.minecraft.sounds.SoundSource;

public final class Config {
	private int frameRateTarget;
	private Map<SoundSource, Float> volumeMultipliers;
	private GraphicsState graphicsState;
	private boolean showToasts;
	private boolean runGarbageCollector;

	public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("frame_rate_target").forGetter(Config::frameRateTarget),
		Codec.unboundedMap(new EnumCodec<>(SoundSource.values()), Codec.FLOAT).fieldOf("volume_multipliers").forGetter(Config::volumeMultipliers),
		new EnumCodec<>(GraphicsState.values()).fieldOf("graphics_state").forGetter(Config::graphicsState),
		Codec.BOOL.fieldOf("show_toasts").forGetter(Config::showToasts),
		Codec.BOOL.fieldOf("run_garbage_collector").forGetter(Config::runGarbageCollector)
	).apply(instance, Config::new));

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

	public Map<SoundSource, Float> volumeMultipliers() {
		return this.volumeMultipliers;
	}

	public float volumeMultiplier(SoundSource category) {
		return this.volumeMultipliers.getOrDefault(category, 1.0f);
	}

	public void setVolumeMultiplier(SoundSource category, float value) {
		this.volumeMultipliers.put(category, value);
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
