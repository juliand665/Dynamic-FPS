package net.lostluma.dynamic_fps.impl.quilt.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dynamic_fps.impl.compat.ClothConfig;
import org.quiltmc.loader.api.QuiltLoader;

public class ModMenu implements ModMenuApi {
	private static final boolean CLOTH_CONFIG = QuiltLoader.isModLoaded("cloth-config2");

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (!CLOTH_CONFIG) {
			return parent -> null;
		} else {
			return ClothConfig::genConfigScreen;
		}
	}
}
