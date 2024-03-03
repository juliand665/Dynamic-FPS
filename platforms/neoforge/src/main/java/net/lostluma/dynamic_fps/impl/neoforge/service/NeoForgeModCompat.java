package net.lostluma.dynamic_fps.impl.neoforge.service;

import dynamic_fps.impl.service.ModCompat;
import net.neoforged.fml.ModList;

public class NeoForgeModCompat implements ModCompat {
	@Override
	public boolean isDisabled() {
		return false;
	}

	@Override
	public boolean disableOverlayOptimization() {
		return ModList.get().isLoaded("rrls");
	}
}
