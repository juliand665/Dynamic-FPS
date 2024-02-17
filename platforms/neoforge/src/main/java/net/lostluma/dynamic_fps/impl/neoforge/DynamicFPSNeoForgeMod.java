package net.lostluma.dynamic_fps.impl.neoforge;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.compat.ClothConfig;
import dynamic_fps.impl.util.HudInfoRenderer;
import dynamic_fps.impl.util.KeyMappingHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class DynamicFPSNeoForgeMod {
    public DynamicFPSNeoForgeMod(IEventBus modEventBus) {
		if (FMLLoader.getDist().isDedicatedServer()) {
			return;
		}

		DynamicFPSMod.init();

		ModLoadingContext.get().registerExtensionPoint(
			ConfigScreenHandler.ConfigScreenFactory.class,
			() -> new ConfigScreenHandler.ConfigScreenFactory(
				(minecraft, screen) -> ClothConfig.genConfigScreen(screen)
			)
		);

		modEventBus.addListener(this::registerKeyMappings);
    }

	public void registerKeyMappings(RegisterKeyMappingsEvent event) {
		for (KeyMappingHandler handler : KeyMappingHandler.getHandlers()) {
			event.register(handler.keyMapping());
		}
	}
}
