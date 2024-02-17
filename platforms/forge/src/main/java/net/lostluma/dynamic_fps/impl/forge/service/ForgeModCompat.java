package net.lostluma.dynamic_fps.impl.forge.service;

import dynamic_fps.impl.service.ModCompat;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraftforge.fml.ModList;

import java.util.HashSet;
import java.util.Set;

public class ForgeModCompat implements ModCompat {
	private static final Set<String> optedInScreens = new HashSet<>();
	private static final Set<String> optedOutScreens = new HashSet<>();

	static {
		optedOutScreens.add(ReceivingLevelScreen.class.getCanonicalName());
	}

	@Override
	public boolean isDisabled() {
		return false;
	}

	@Override
	public boolean disableOverlayOptimization() {
		return ModList.get().isLoaded("rrls");
	}

	@Override
	public Set<String> getOptedInScreens() {
		return optedInScreens;
	}

	@Override
	public Set<String> getOptedOutScreens() {
		return optedOutScreens;
	}
}
