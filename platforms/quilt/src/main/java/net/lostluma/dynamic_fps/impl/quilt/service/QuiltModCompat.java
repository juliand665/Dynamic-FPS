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

	private static void parseModMetadata(ModContainer mod) {
		LoaderValue.LObject root;
		ModMetadata data = mod.metadata();

		try {
			root = data.value("dynamic_fps").asObject();
		} catch (ClassCastException | NullPointerException e) {
			return; // Object is either missing or is of an invalid type
		}

		parseOverlayOverride(root.get("optimized_overlay"));
	}

	private static void parseOverlayOverride(@Nullable LoaderValue value) {
		if (value != null && value.type() == LoaderValue.LType.BOOLEAN && !value.asBoolean()) {
			disableOverlayOptimization = true;
		}
	}
}
