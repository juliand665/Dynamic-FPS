package net.lostluma.dynamic_fps.impl.neoforge.service;

import com.electronwill.nightconfig.core.CommentedConfig;
import dynamic_fps.impl.service.ModCompat;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

public class NeoForgeModCompat implements ModCompat {
	private static boolean disableOverlayOptimization = false;

	static {
		ModList.get().getMods().forEach(NeoForgeModCompat::parseModMetadata);
	}

	@Override
	public boolean isDisabled() {
		return false;
	}

	@Override
	public boolean disableOverlayOptimization() {
		return disableOverlayOptimization || ModList.get().isLoaded("rrls");
	}

	private static void parseModMetadata(IModInfo modInfo) {
		Object config = modInfo.getModProperties().get("dynamic_fps");

		if (config == null) {
			return;
		}

		Boolean value = ((CommentedConfig) config).get("optimized_overlay");

		if (value != null && !value) {
			disableOverlayOptimization = true;
		}
	}
}
