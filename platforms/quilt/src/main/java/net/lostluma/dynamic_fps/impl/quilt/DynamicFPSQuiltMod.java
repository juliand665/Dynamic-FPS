package net.lostluma.dynamic_fps.impl.quilt;

import dynamic_fps.impl.DynamicFPSMod;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class DynamicFPSQuiltMod implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		DynamicFPSMod.init();
	}
}
