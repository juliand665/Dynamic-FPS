package net.lostluma.dynamic_fps.impl.neoforge.service;

import dynamic_fps.impl.service.ModCompat;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
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

	@Override
	public boolean isScreenOptedIn(String className) {
		return false;
	}

	@Override
	public boolean isScreenOptedOut(String className) {
		return ReceivingLevelScreen.class.getCanonicalName().equals(className);
	}
}
