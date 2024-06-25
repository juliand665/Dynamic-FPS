package net.lostluma.dynamic_fps.impl.quilt.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dynamic_fps.impl.DynamicFPSMod;

public class ModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return DynamicFPSMod::getConfigScreen;
	}
}
