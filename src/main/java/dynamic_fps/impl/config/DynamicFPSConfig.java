package dynamic_fps.impl.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.GraphicsState;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.util.EnumCodec;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sounds.SoundSource;

public final class DynamicFPSConfig {
	private Map<PowerState, Config> configs;

	private static final Path CONFIGS = FabricLoader.getInstance().getConfigDir();
	private static final Path CONFIG_FILE = CONFIGS.resolve(DynamicFPSMod.MOD_ID + ".json");

	private static final Codec<Map<PowerState, Config>> STATES_CODEC = Codec.unboundedMap(new EnumCodec<>(PowerState.values()), Config.CODEC);

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
		if (state == PowerState.FOCUSED) {
			return Config.ACTIVE;
		} else {
			return configs.get(state);
		}
	}

	private Map<PowerState, Config> configs() {
		return this.configs;
	}

	public static DynamicFPSConfig load() {
		String data;

		try {
			data = Files.readString(CONFIG_FILE);
		} catch (NoSuchFileException e) {
			var config = new DynamicFPSConfig(new EnumMap<>(PowerState.class));
			config.save();
			return config;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Dynamic FPS config.", e);
		}

		var root = JsonParser.parseString(data);

		upgradeConfig((JsonObject) root);
		var parsed = CODEC.parse(JsonOps.INSTANCE, root);

		return parsed.getOrThrow(false, RuntimeException::new);
	}

	public void save() {
		var data = CODEC.encodeStart(JsonOps.INSTANCE, this);
		var root = data.getOrThrow(false, RuntimeException::new);

		try {
			var temp = Files.createTempFile(CONFIGS, "dynamic_fps", ".json");
			Files.writeString(temp, root.toString(), StandardCharsets.UTF_8);

			Files.deleteIfExists(CONFIG_FILE);
			Files.move(temp, CONFIG_FILE, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			// Cloth Config's built-in saving does not support catching exceptions :(
			throw new RuntimeException("Failed to save or modify Dynamic FPS config!", e);
		}
	}

	public static Config getDefaultConfig(PowerState state) {
		switch (state) {
			case HOVERED: {
				return new Config(60, withMasterVolume(1.0f), GraphicsState.DEFAULT, true, false);
			}
			case UNFOCUSED: {
				return new Config(1, withMasterVolume(0.25f), GraphicsState.DEFAULT, false, false);
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
		var volumes = new HashMap<SoundSource, Float>();
		volumes.put(SoundSource.MASTER, value);
		return volumes;
	}

	private static void upgradeConfig(JsonObject root) {
		upgradeVolumeMultiplier(root);
	}

	private static void upgradeVolumeMultiplier(JsonObject root) {
		// Convert each old power state config
		// - { "volume_multiplier": 0.0, ... }
		// + { "volume_multipliers": { "master": 0.0 }, ... }
		if (!root.has("states")) {
			return;
		}

		var states = root.getAsJsonObject("states");

		if (!states.isJsonObject()) {
			return;
		}

		for (var key : states.keySet()) {
			var element = states.getAsJsonObject(key);

			if (!element.isJsonObject()) {
				continue;
			}

			if (!element.has("volume_multiplier")) {
				continue;
			}

			var multiplier = element.get("volume_multiplier");

			if (!multiplier.isJsonPrimitive() || !((JsonPrimitive)multiplier).isNumber()) {
				continue;
			}

			var multipliers = new JsonObject();
			multipliers.add("master", multiplier);

			element.add("volume_multipliers", multipliers);
		}
	}
}
