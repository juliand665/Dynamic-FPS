package dynamicfps.util;

import dynamicfps.DynamicFPSMod;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class Localization {
	/** e.g. keyString("title", "config") -> "title.dynamicfps.config") */
	public static String translationKey(String domain, String path) {
		return domain + "." + DynamicFPSMod.MOD_ID + "." + path;
	}
	
	public static Text localized(String domain, String path, Object... args) {
		return new TranslatableText(translationKey(domain, path), args);
	}
	
	private Localization() {}
}
