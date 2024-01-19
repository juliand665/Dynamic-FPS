package dynamic_fps.impl.util;

import java.util.HashSet;
import java.util.Set;

import net.fabricmc.loader.api.MappingResolver;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.CustomValue.CvObject;
import net.fabricmc.loader.api.metadata.CustomValue.CvType;

public class ModCompatibility {
	private static boolean disableOverlayOptimization = false;

	private static Set<String> optedInScreens = new HashSet<>();
	private static Set<String> optedOutScreens = new HashSet<>();

	static {
		FabricLoader.getInstance().getAllMods().forEach(ModCompatibility::parseModMetadata);
	}

	public static boolean disableOverlayOptimization() {
		return disableOverlayOptimization;
	}

	public static boolean isScreenOptedIn(String className) {
		return optedInScreens.contains(className);
	}

	public static boolean isScreenOptedOut(String className) {
		return optedOutScreens.contains(className);
	}

	private static void parseModMetadata(ModContainer mod) {
		CvObject root;
		ModMetadata data = mod.getMetadata();

		try {
			root = data.getCustomValue("dynamic_fps").getAsObject();
		} catch (ClassCastException | NullPointerException e) {
			return; // Object is either missing or is of an invalid type
		}

		parseOverlayOverride(root.get("optimized_overlay"));

		parseScreenOverrides(root.get("optimized_screens"), "enabled", optedInScreens);
		parseScreenOverrides(root.get("optimized_screens"), "disabled", optedOutScreens);
	}

	private static void parseOverlayOverride(@Nullable CustomValue value) {
		if (value != null && value.getType() == CvType.BOOLEAN && !value.getAsBoolean()) {
			disableOverlayOptimization = true;
		}
	}

	private static void parseScreenOverrides(@Nullable CustomValue parent, String type, Set<String> set) {
		if (parent == null || parent.getType() != CvType.OBJECT) {
			return;
		}

		CustomValue values = parent.getAsObject().get(type);

		if (values == null || values.getType() != CvType.ARRAY) {
			return;
		}

		MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

		values.getAsArray().forEach(value -> {
			if (value.getType() == CvType.STRING) {
				// Translate from intermediary to runtime names for vanilla screens
				set.add(resolver.mapClassName("intermediary", value.getAsString()));
			}
		});
	}
}
