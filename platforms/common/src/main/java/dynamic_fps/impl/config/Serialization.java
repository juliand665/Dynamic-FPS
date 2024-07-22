package dynamic_fps.impl.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dynamic_fps.impl.Constants;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.Logging;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;

public class Serialization {
	private static final Gson GSON = new GsonBuilder()
		.setLenient()
		.serializeNulls()
		.setPrettyPrinting()
		.enableComplexMapKeySerialization()
		.registerTypeHierarchyAdapter(Enum.class, new EnumSerializer<>())
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.create();

	private static final String CONFIG_FILE = Constants.MOD_ID + ".json";

	public static void save(DynamicFPSConfig instance) {
		JsonObject config = (JsonObject) GSON.toJsonTree(instance);
		JsonObject parent = (JsonObject) GSON.toJsonTree(DynamicFPSConfig.DEFAULTS);

		String data = GSON.toJson(removeUnchangedFields(config, parent)) + "\n";

		Path cache = Platform.getInstance().getCacheDir();
		Path configs = Platform.getInstance().getConfigDir().resolve(CONFIG_FILE);

		try {
			Path temp = Files.createTempFile(cache, "config", ".json");

			Files.write(temp, data.getBytes(StandardCharsets.UTF_8));
			Serialization.move(temp, configs); // Attempt atomic move, fall back otherwise
		} catch (IOException e) {
			// Cloth Config's built-in saving does not support catching exceptions :(
			throw new RuntimeException("Failed to save or modify Dynamic FPS config!", e);
		}
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
		return GSON.fromJson(root, DynamicFPSConfig.class); // Ignores regular constructor!
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

		return GSON.fromJson(new String(data, StandardCharsets.UTF_8), DynamicFPSConfig.class);
	}

	private static void upgradeConfig(JsonObject config) {
		// v3.3.0
		upgradeVolumeMultiplier(config);

		// v3.5.0
		upgradeIdleConfig(config);

		// version agnostic
		addMissingFields(config, (JsonObject) GSON.toJsonTree(DynamicFPSConfig.DEFAULTS));
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

	private static @Nullable JsonObject getStatesAsObject(JsonObject root) {
		if (!root.has("states")) {
			return null;
		}

		if (!root.get("states").isJsonObject()) {
			return null;
		}

		return root.getAsJsonObject("states");
	}

	private static final class EnumSerializer<T extends Enum<T>> implements JsonSerializer<T>, JsonDeserializer<T> {
		@Override
		public JsonElement serialize(T instance, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(instance.toString().toLowerCase(Locale.ROOT));
		}

		@Override
		public T deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			try {
				Class<T> class_ = (Class<T>) Class.forName(type.getTypeName());
				return Enum.valueOf(class_, element.getAsString().toUpperCase(Locale.ROOT));
			} catch (ClassNotFoundException | IllegalArgumentException e) {
				throw new JsonParseException(e);
			}
		}
	}
}
