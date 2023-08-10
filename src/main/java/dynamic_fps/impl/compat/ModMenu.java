package dynamic_fps.impl.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dynamic_fps.impl.config.ClothConfigScreenFactory;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			return parent -> null;
		} else {
			return ClothConfigScreenFactory::genConfig;
		}
	}
}
