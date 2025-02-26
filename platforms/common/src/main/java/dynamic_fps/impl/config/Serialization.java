package dynamic_fps.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dynamic_fps.impl.Constants;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.JsonUtil;
import dynamic_fps.impl.util.Logging;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class Serialization {
	private static final String CONFIG_FILE = Constants.MOD_ID + ".json";

	public static void save() {
		JsonObject config = (JsonObject) JsonUtil.toJsonTree(DynamicFPSConfig.INSTANCE);
		JsonObject parent = (JsonObject) JsonUtil.toJsonTree(DynamicFPSConfig.DEFAULTS);

		String data = JsonUtil.toJson(removeUnchangedFields(config, parent));

		try {
			write(data);
		} catch (IOException e) {
			// Cloth Config's built-in saving does not support catching exceptions :(
			throw new RuntimeException("Failed to save or modify Dynamic FPS config!", e);
		}
	}

	private static void write(String data) throws IOException {
		data = data + "\n";
		Platform platform = Platform.getInstance();

		Path cache = Platform.getInstance().getCacheDir();
		Path maybe = Files.createTempFile(cache, "config", ".json");

		Files.write(maybe, data.getBytes(StandardCharsets.UTF_8));
		Serialization.move(maybe, platform.getConfigDir().resolve(CONFIG_FILE));
	}

	private static void move(Path from, Path to) throws IOException {
		try {
			Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException | UnsupportedOperationException e) {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }

	private static JsonObject removeUnchangedFields(JsonObject config, JsonObject parent) {
		// Recursively delete all fields that are equal to the defaults ...

		parent.entrySet().forEach(entry -> {
			String name = entry.getKey();

			JsonElement other = entry.getValue();
			JsonElement value = config.get(name);

			if (value == null) {
				return;
			}

			if (value.isJsonObject() && other.isJsonObject()) {
				removeUnchangedFields((JsonObject) value, (JsonObject) other);
			}

			if (value.equals(other) || (value.isJsonObject() && value.getAsJsonObject().size() == 0)) {
				config.remove(name);
			}
		});

		return config;
	}

	@SuppressWarnings("deprecation")
	public static DynamicFPSConfig loadPersonalized() {
		byte[] data = null;
		Path config = Platform.getInstance().getConfigDir().resolve(CONFIG_FILE);

		try {
			data = Files.readAllBytes(config);
		} catch (NoSuchFileException e) {
			// Use default config

			// Try to create an empty config file
			// Prevents the "no config" warning next startup
			try {
				write("{}");
			} catch (IOException ex) {
				// At least we tried ...
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Dynamic FPS config.", e);
		}

		JsonObject root;

		// Sometimes failing to save the config produces a file of only null bytes (on Windows?).
		// Since there's no point in crashing just reset the config to the default state instead.
		if (data == null || data[0] == 0) {
			root = new JsonObject();
			Logging.getLogger().warn("Dynamic FPS config missing or corrupted! Using defaults.");
		} else {
			root = (JsonObject) new JsonParser().parse(new String(data, StandardCharsets.UTF_8));
		}

		upgradeConfig(root);
		return JsonUtil.fromJson(root, DynamicFPSConfig.class); // Ignores regular constructor!
	}

	public static DynamicFPSConfig loadDefault() {
		byte[] data;

		try (InputStream stream = Serialization.class.getResourceAsStream("/assets/dynamic_fps/data/default_config.json")) {
			if (stream == null) {
				throw new IOException("Stream is null.");
			}

			data = stream.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Dynamic FPS config.", e);
		}

		return JsonUtil.fromJson(new String(data, StandardCharsets.UTF_8), DynamicFPSConfig.class);
	}

	private static void upgradeConfig(JsonObject config) {
		// v3.3.0
		upgradeVolumeMultiplier(config);

		// v3.5.0
		upgradeIdleConfig(config);

		// v3.9.0
		upgradeBatteryNotificationConfig(config);

		// version agnostic
		addMissingFields(config, (JsonObject) JsonUtil.toJsonTree(DynamicFPSConfig.DEFAULTS));
	}

	private static void addMissingFields(JsonObject config, JsonObject parent) {
		// Recursively add all fields that are missing from the user config

		parent.entrySet().forEach(entry -> {
			String name = entry.getKey();

			JsonElement other = entry.getValue();
			JsonElement value = config.get(name);

			if (value == null) {
				config.add(name, other);
			} else if (value.isJsonObject() && other.isJsonObject()) {
				addMissingFields((JsonObject) value, (JsonObject) other);
			}
		});
	}

	private static void upgradeVolumeMultiplier(JsonObject root) {
		// Convert each old power state config
		// - { "volume_multiplier": 0.0, ... }
		// + { "volume_multipliers": { "master": 0.0 }, ... }
		JsonObject states = getStatesAsObject(root);

		if (states == null) {
			return;
		}

		for (Map.Entry<String, JsonElement> entry : states.entrySet()) {
			JsonElement value = entry.getValue();

			if (!value.isJsonObject()) {
				continue;
			}

			JsonObject element = value.getAsJsonObject();

			if (!element.has("volume_multiplier")) {
				continue;
			}

			JsonElement multiplier = element.get("volume_multiplier");

			if (!multiplier.isJsonPrimitive() || !((JsonPrimitive) multiplier).isNumber()) {
				continue;
			}

			JsonObject multipliers = new JsonObject();
			multipliers.add("master", multiplier);

			element.add("volume_multipliers", multipliers);
		}
	}

	private static void upgradeIdleConfig(JsonObject root) {
		// Replace idle_time field with the new object
		if (!root.has("idle_time")) {
			return;
		}

		JsonElement idleTime = root.get("idle_time");

		if (!idleTime.isJsonPrimitive() || !idleTime.getAsJsonPrimitive().isNumber()) {
			return;
		}

		int timeout = idleTime.getAsInt();

		// The setting is unused, so no need to migrate
		// Instead the new default value from the JAR will overwrite it
		if (timeout == 0) {
			return;
		}

		JsonObject idle = new JsonObject();

		idle.addProperty("timeout", timeout);
		idle.addProperty("condition", "none");

		root.add("idle", idle);
	}

	private static void upgradeBatteryNotificationConfig(JsonObject root) {
		// Convert battery notification to object
		// - { "notifications": true, ... }
		// + { "notifications": { "enabled": true, "percent": 10 }, ... }
		if (!root.has("battery_tracker")) {
			return;
		}

		JsonObject battery = root.getAsJsonObject("battery_tracker");

		if (!battery.has("notifications")) {
			return;
		}

		JsonElement field = battery.get("notifications");

		if (!field.isJsonPrimitive() || !field.getAsJsonPrimitive().isBoolean()) {
			return;
		}

		JsonObject notifications = new JsonObject();
		notifications.add("enabled", field);

		battery.add("notifications", notifications);
	}

	private static @Nullable JsonObject getStatesAsObject(JsonObject root) {
		if (!root.has("states")) {
			return null;
		}

		if (!root.get("states").isJsonObject()) {
			return null;
		}

		return root.getAsJsonObject("states");
	}
}
