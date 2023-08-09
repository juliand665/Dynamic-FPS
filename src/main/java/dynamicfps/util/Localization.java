package dynamicfps.util;

import dynamicfps.DynamicFPSMod;
import net.minecraft.network.chat.Component;

public final class Localization {
	/** e.g. keyString("title", "config") -> "title.dynamicfps.config") */
	public static String translationKey(String domain, String path) {
		return domain + "." + DynamicFPSMod.MOD_ID + "." + path;
	}

	public static Component localized(String domain, String path, Object... args) {
		return Component.translatable(translationKey(domain, path), args);
	}

	private Localization() {}
}
