package net.lostluma.dynamic_fps.impl.neoforge;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.util.HudInfoRenderer;
import dynamic_fps.impl.util.KeyMappingHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class DynamicFPSNeoForgeMod {
    public DynamicFPSNeoForgeMod(IEventBus modEventBus) {
		if (FMLLoader.getCurrent().getDist().isDedicatedServer()) {
			return;
		}

		DynamicFPSMod.init();

		ModLoadingContext.get().registerExtensionPoint(
			IConfigScreenFactory.class,
			() -> (minecraft, screen) -> DynamicFPSMod.getConfigScreen(screen)
		);

		modEventBus.addListener(this::registerKeyMappings);
		NeoForge.EVENT_BUS.addListener(this::renderGuiOverlay);
    }

	public void renderGuiOverlay(RenderGuiEvent.Pre event) {
		HudInfoRenderer.renderInfo(event.getGuiGraphics());
	}

	public void registerKeyMappings(RegisterKeyMappingsEvent event) {
		for (KeyMappingHandler handler : KeyMappingHandler.getHandlers()) {
			event.register(handler.keyMapping());
		}
	}
}
