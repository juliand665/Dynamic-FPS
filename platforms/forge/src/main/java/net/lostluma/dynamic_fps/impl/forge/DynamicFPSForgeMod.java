package net.lostluma.dynamic_fps.impl.forge;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.compat.ClothConfig;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.KeyMappingHandler;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
			ConfigGuiHandler.ConfigGuiFactory.class,
			() -> new ConfigGuiHandler.ConfigGuiFactory(
				(minecraft, screen) -> ClothConfig.genConfigScreen(screen)
			)
		);

		MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeyMappings);
	}

	public void registerKeyMappings(FMLClientSetupEvent event) {
		for (KeyMappingHandler handler : KeyMappingHandler.getHandlers()) {
			ClientRegistry.registerKeyBinding(handler.keyMapping());
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
