package dynamic_fps.impl.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.GraphicsState;
import dynamic_fps.impl.PowerState;
import net.fabricmc.loader.api.FabricLoader;

public final class DynamicFPSConfig {
	private Map<PowerState, Config> configs;

	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(DynamicFPSMod.MOD_ID + ".json");
	private static final Codec<Map<PowerState, Config>> STATES_CODEC = Codec.unboundedMap(PowerState.CODEC, Config.CODEC);

	private static final Codec<DynamicFPSConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		STATES_CODEC.fieldOf("states").forGetter(DynamicFPSConfig::configs)
	).apply(instance, DynamicFPSConfig::new));

	private DynamicFPSConfig(Map<PowerState, Config> configs) {
		this.configs = new EnumMap<>(configs);

		for (var state : PowerState.values()) {
			if (state.configurable) {
				this.configs.computeIfAbsent(state, DynamicFPSConfig::getDefaultConfig);
			}
		}
	}

	public Config get(PowerState state) {
		switch (state) {
			case FOCUSED: {
				return Config.ACTIVE;
			}
			case SUSPENDED: {
				return Config.SUSPENDED;
			}
			default: {
				return configs.get(state);
			}
		}
	}

	private Map<PowerState, Config> configs() {
		return this.configs;
	}

	public static DynamicFPSConfig load() {
		String data;

		try {
			data = Files.readString(PATH);
		} catch (NoSuchFileException e) {
			return new DynamicFPSConfig(new EnumMap<>(PowerState.class));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Dynamic FPS config.", e);
		}

		var root = JsonParser.parseString(data);
		var parsed = CODEC.parse(JsonOps.INSTANCE, root);

		return parsed.getOrThrow(false, RuntimeException::new);
	}

	public void save() {
		var data = CODEC.encodeStart(JsonOps.INSTANCE, this);
		var root = data.getOrThrow(false, RuntimeException::new);

		try {
			Files.writeString(PATH, root.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// Cloth Config's automatic saving does not support catching exceptions
			throw new RuntimeException("Failed to save Dynamic FPS config.", e);
		}
	}

	public static Config getDefaultConfig(PowerState state) {
		switch (state) {
			case HOVERED: {
				return new Config(60, 1.0f, GraphicsState.DEFAULT, true, false);
			}
			case UNFOCUSED: {
				return new Config(1, 0.25f, GraphicsState.DEFAULT, false, false);
			}
			case INVISIBLE: {
				return new Config(0, 0.0f, GraphicsState.DEFAULT, false, false);
			}
			default: {
				throw new RuntimeException("Getting default configuration for unhandled power state " + state.toString());
			}
		}
	}
}
