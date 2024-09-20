package net.lostluma.dynamic_fps.impl.textile;

import dynamic_fps.impl.DynamicFPSMod;
import net.fabricmc.api.ClientModInitializer;

public class DynamicFPSTextileMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		DynamicFPSMod.init();
	}
}
