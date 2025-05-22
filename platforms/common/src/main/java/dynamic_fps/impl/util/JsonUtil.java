package dynamic_fps.impl.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Locale;

public class JsonUtil {
	@SuppressWarnings("deprecation")
	private static final Gson GSON = new GsonBuilder()
		.setLenient()
		.serializeNulls()
		.setPrettyPrinting()
		.enableComplexMapKeySerialization()
		.registerTypeHierarchyAdapter(Enum.class, new EnumSerializer<>())
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.create();

	public static String toJson(Object object) {
		return GSON.toJson(object);
	}

	public static String toJson(JsonElement element) {
		return GSON.toJson(element);
	}

	public static JsonElement toJsonTree(Object object) {
		return GSON.toJsonTree(object);
	}

	public static <T> T fromJson(String data, Class<T> type) {
		return GSON.fromJson(data, type);
	}

	public static <T> T fromJson(JsonElement data, Class<T> type) {
		return GSON.fromJson(data, type);
	}

	private static final class EnumSerializer<T extends Enum<T>> implements JsonSerializer<T>, JsonDeserializer<T> {
		@Override
		public JsonElement serialize(T instance, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(instance.toString().toLowerCase(Locale.ROOT));
		}

		@Override
		public T deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			try {
				@SuppressWarnings("unchecked")
				Class<T> class_ = (Class<T>) Class.forName(type.getTypeName());
				return Enum.valueOf(class_, element.getAsString().toUpperCase(Locale.ROOT));
			} catch (ClassNotFoundException | IllegalArgumentException e) {
				throw new JsonParseException(e);
			}
		}
	}
}
