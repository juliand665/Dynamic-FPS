package net.lostluma.dynamic_fps.impl.forge;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.compat.ClothConfig;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.HudInfoRenderer;
import dynamic_fps.impl.util.KeyMappingHandler;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.List;

@Mod(Constants.MOD_ID)
public class DynamicFPSForgeMod {
	private static final List<Platform.StartTickEvent> TICK_EVENT_LISTENERS = new ArrayList<>();

    public DynamicFPSForgeMod() {
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

		MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
		MinecraftForge.EVENT_BUS.addListener(this::renderGuiOverlay);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeyMappings);
    }

	public void renderGuiOverlay(RenderGuiOverlayEvent event) {
		HudInfoRenderer.renderInfo(event.getPoseStack());
	}

	public void registerKeyMappings(RegisterKeyMappingsEvent event) {
		for (KeyMappingHandler handler : KeyMappingHandler.getHandlers()) {
			event.register(handler.keyMapping());
		}
	}

	public void onClientTick(TickEvent.ClientTickEvent event) {
		for (Platform.StartTickEvent listener : TICK_EVENT_LISTENERS) {
			listener.onStartTick();
		}
	}

	public static void addTickEventListener(Platform.StartTickEvent event) {
		TICK_EVENT_LISTENERS.add(event);
	}
}
