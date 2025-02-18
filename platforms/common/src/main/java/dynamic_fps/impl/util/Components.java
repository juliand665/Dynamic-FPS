package dynamic_fps.impl.util;

import dynamic_fps.impl.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class Components {
	/** e.g. keyString("title", "config") -> "title.dynamic_fps.config") */
	public static String translationKey(String domain, String path) {
		return domain + "." + Constants.MOD_ID + "." + path;
	}

	public static MutableComponent literal(String value) {
		return Component.literal(value);
	}

	public static MutableComponent translatable(String path, Object... args) {
		return Component.translatable(path, args);
	}

	public static MutableComponent translatable(String domain, String path, Object... args) {
		return Component.translatable(translationKey(domain, path), args);
	}
}
