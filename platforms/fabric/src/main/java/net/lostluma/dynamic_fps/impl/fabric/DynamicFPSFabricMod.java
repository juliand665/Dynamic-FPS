package net.lostluma.dynamic_fps.impl.fabric;

import dynamic_fps.impl.DynamicFPSMod;
import net.fabricmc.api.ClientModInitializer;

public class DynamicFPSFabricMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		DynamicFPSMod.init();
	}
}
