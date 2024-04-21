package net.lostluma.dynamic_fps.impl.fabric.service;

import dynamic_fps.impl.service.ModCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.lostluma.dynamic_fps.impl.fabric.compat.FREX;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class FabricModCompat implements ModCompat {
	private static boolean disableOverlayOptimization = false;

	private static final Set<String> optedInScreens = new HashSet<>();
	private static final Set<String> optedOutScreens = new HashSet<>();

	static {
		FabricLoader.getInstance().getAllMods().forEach(FabricModCompat::parseModMetadata);
	}

	@Override
	public boolean isDisabled() {
		return FREX.isFlawlessFramesActive();
	}

	@Override
	public boolean disableOverlayOptimization() {
		return disableOverlayOptimization;
	}


	@Override
	public Set<String> getOptedInScreens() {
		return optedInScreens;
	}

	@Override
	public Set<String> getOptedOutScreens() {
		return optedOutScreens;
	}

	private static void parseModMetadata(ModContainer mod) {
		CustomValue.CvObject root;
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
		if (value != null && value.getType() == CustomValue.CvType.BOOLEAN && !value.getAsBoolean()) {
			disableOverlayOptimization = true;
		}
	}

	private static void parseScreenOverrides(@Nullable CustomValue parent, String type, Set<String> set) {
		if (parent == null || parent.getType() != CustomValue.CvType.OBJECT) {
			return;
		}

		CustomValue values = parent.getAsObject().get(type);

		if (values == null || values.getType() != CustomValue.CvType.ARRAY) {
			return;
		}

		MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

		values.getAsArray().forEach(value -> {
			if (value.getType() == CustomValue.CvType.STRING) {
				// Translate from intermediary to runtime names for vanilla screens
				set.add(resolver.mapClassName("intermediary", value.getAsString()));
			}
		});
	}
}
