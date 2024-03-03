package net.lostluma.dynamic_fps.impl.fabric.service;

import dynamic_fps.impl.service.ModCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.lostluma.dynamic_fps.impl.fabric.compat.FREX;
import org.jetbrains.annotations.Nullable;

public class FabricModCompat implements ModCompat {
	private static boolean disableOverlayOptimization = false;

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

	private static void parseModMetadata(ModContainer mod) {
		CustomValue.CvObject root;
		ModMetadata data = mod.getMetadata();

		try {
			root = data.getCustomValue("dynamic_fps").getAsObject();
		} catch (ClassCastException | NullPointerException e) {
			return; // Object is either missing or is of an invalid type
		}

		parseOverlayOverride(root.get("optimized_overlay"));
	}

	private static void parseOverlayOverride(@Nullable CustomValue value) {
		if (value != null && value.getType() == CustomValue.CvType.BOOLEAN && !value.getAsBoolean()) {
			disableOverlayOptimization = true;
		}
	}
}
