package dynamic_fps.impl.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

public class ModMenu implements ModMenuApi {
	private static final boolean CLOTH_CONFIG = FabricLoader.getInstance().isModLoaded("cloth-config2");

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (!CLOTH_CONFIG) {
			return parent -> null;
		} else {
			return ClothConfig::genConfigScreen;
		}
	}
}
