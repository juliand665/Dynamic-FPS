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
import dynamic_fps.impl.GraphicsState;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.service.Platform;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class Serialization {
	private static final Gson GSON = new GsonBuilder()
		.setLenient()
		.serializeNulls()
		.setPrettyPrinting()
		.enableComplexMapKeySerialization()
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.registerTypeAdapter(PowerState.class, new PowerStateSerializer())
		.registerTypeAdapter(GraphicsState.class, new GraphicsStateSerializer())
		.create();

	private static final String CONFIG_FILE = Constants.MOD_ID + ".json";

	public static void save(DynamicFPSConfig instance) {
		String data = GSON.toJson(instance) + "\n";

		Path cache = Platform.getInstance().getCacheDir();
		Path config = Platform.getInstance().getConfigDir().resolve(CONFIG_FILE);

		try {
			Path temp = Files.createTempFile(cache, "config", ".json");

			Files.write(temp, data.getBytes(StandardCharsets.UTF_8));
			Files.move(temp, config, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			// Cloth Config's built-in saving does not support catching exceptions :(
			throw new RuntimeException("Failed to save or modify Dynamic FPS config!", e);
		}
	}

	@SuppressWarnings("deprecation")
	public static DynamicFPSConfig load() {
		byte[] data;
		Path config = Platform.getInstance().getConfigDir().resolve(CONFIG_FILE);

		try {
			data = Files.readAllBytes(config);
		} catch (NoSuchFileException e) {
			DynamicFPSConfig instance = new DynamicFPSConfig(
				true,
				0,
				false,
				new EnumMap<>(PowerState.class)
			);
			instance.save();
			return instance;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Dynamic FPS config.", e);
		}

		JsonElement root = new JsonParser().parse(new String(data, StandardCharsets.UTF_8));

		upgradeConfig((JsonObject) root);
		return GSON.fromJson(root, DynamicFPSConfig.class); // Ignores regular constructor!
	}

	private static void upgradeConfig(JsonObject root) {
		addIdleTime(root);
		upgradeVolumeMultiplier(root);
		addAbandonedConfig(root);
		addUncapMenuFrameRate(root);
		addEnabled(root);
	}

	private static void addIdleTime(JsonObject root) {
		// Add idle_time field if it's missing
		if (!root.has("idle_time")) {
			root.addProperty("idle_time", 0);
		}
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

	private static void addAbandonedConfig(JsonObject root) {
		// Add default config for abandoned power state
		JsonObject states = getStatesAsObject(root);

		if (states == null) {
			return;
		}

		if (states.has("abandoned")) {
			return;
		}

		states.add("abandoned", GSON.toJsonTree(Config.getDefault(PowerState.ABANDONED)));
	}

	private static void addUncapMenuFrameRate(JsonObject root) {
		// Add uncap_menu_frame_rate field if it's missing
		if (!root.has("uncap_menu_frame_rate")) {
			root.addProperty("uncap_menu_frame_rate", false);
		}
	}

	private static void addEnabled(JsonObject root) {
		// Add enabled field if it's missing
		if (!root.has("enabled")) {
			root.addProperty("enabled", true);
		}
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

	private static final class PowerStateSerializer implements JsonSerializer<PowerState>, JsonDeserializer<PowerState> {
		@Override
		public JsonElement serialize(PowerState state, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(state.toString().toLowerCase(Locale.ROOT));
		}

		@Override
		public PowerState deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			return PowerState.valueOf(element.getAsString().toUpperCase(Locale.ROOT));
		}
	}

	private static final class GraphicsStateSerializer implements JsonSerializer<GraphicsState>, JsonDeserializer<GraphicsState> {
		@Override
		public JsonElement serialize(GraphicsState state, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(state.toString().toLowerCase(Locale.ROOT));
		}

		@Override
		public GraphicsState deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			return GraphicsState.valueOf(element.getAsString().toUpperCase(Locale.ROOT));
		}
	}
}
