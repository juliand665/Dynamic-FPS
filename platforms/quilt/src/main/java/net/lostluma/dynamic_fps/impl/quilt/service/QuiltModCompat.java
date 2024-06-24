package net.lostluma.dynamic_fps.impl.quilt.service;

import dynamic_fps.impl.service.ModCompat;
import net.lostluma.dynamic_fps.impl.quilt.compat.FREX;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.LoaderValue;
import org.quiltmc.loader.api.MappingResolver;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.ModMetadata;
import org.quiltmc.loader.api.QuiltLoader;

import java.util.HashSet;
import java.util.Set;

public class QuiltModCompat implements ModCompat {
	private static boolean disableOverlayOptimization = false;

	private static final Set<String> optedInScreens = new HashSet<>();
	private static final Set<String> optedOutScreens = new HashSet<>();

	static {
		QuiltLoader.getAllMods().forEach(QuiltModCompat::parseModMetadata);
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
		LoaderValue.LObject root;
		ModMetadata data = mod.metadata();

		try {
			root = data.value("dynamic_fps").asObject();
		} catch (ClassCastException | NullPointerException e) {
			return; // Object is either missing or is of an invalid type
		}

		parseOverlayOverride(root.get("optimized_overlay"));

		parseScreenOverrides(root.get("optimized_screens"), "enabled", optedInScreens);
		parseScreenOverrides(root.get("optimized_screens"), "disabled", optedOutScreens);
	}

	private static void parseOverlayOverride(@Nullable LoaderValue value) {
		if (value != null && value.type() == LoaderValue.LType.BOOLEAN && !value.asBoolean()) {
			disableOverlayOptimization = true;
		}
	}

	private static void parseScreenOverrides(@Nullable LoaderValue parent, String type, Set<String> set) {
		if (parent == null || parent.type() != LoaderValue.LType.OBJECT) {
			return;
		}

		LoaderValue values = parent.asObject().get(type);

		if (values == null || values.type() != LoaderValue.LType.ARRAY) {
			return;
		}

		MappingResolver resolver = QuiltLoader.getMappingResolver();

		values.asArray().forEach(value -> {
			if (value.type() == LoaderValue.LType.STRING) {
				// Translate from intermediary to runtime names for vanilla screens
				set.add(resolver.mapClassName("intermediary", value.asString()));
			}
		});
	}
}
