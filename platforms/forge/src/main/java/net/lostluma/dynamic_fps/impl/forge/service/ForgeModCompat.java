package net.lostluma.dynamic_fps.impl.forge.service;

import dynamic_fps.impl.service.ModCompat;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraftforge.fml.ModList;

public class ForgeModCompat implements ModCompat {
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
