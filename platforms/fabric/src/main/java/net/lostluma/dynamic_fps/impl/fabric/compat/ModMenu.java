package net.lostluma.dynamic_fps.impl.fabric.compat;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.compat.ClothConfig;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Function;

public class ModMenu implements ModMenuApi {
	private static final boolean CLOTH_CONFIG = FabricLoader.getInstance().isModLoaded("cloth-config2");

	@Override
	public String getModId() {
		return Constants.MOD_ID;
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		if (!CLOTH_CONFIG) {
			return (parent -> null);
		} else {
			return (ClothConfig::genConfigScreen);
		}
	}
}
