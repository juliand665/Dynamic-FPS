package net.lostluma.dynamic_fps.impl.forge.compat;

import dynamic_fps.impl.Constants;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reimplementation of FREX class from Fabric part
 * @see net.minecraftforge.client.ConfigScreenHandler
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public final class FREX {
	private static final AtomicBoolean ACTIVE = new AtomicBoolean();

	public static boolean isFlawlessFramesActive() {
		return ACTIVE.get();
	}

	@SubscribeEvent
	private static void init(FMLClientSetupEvent event) {
		ModList.get().forEachModContainer(
			(id, container) -> {
				if (!ACTIVE.get()) {
					container.getCustomExtension(FrexExtension.Factory.class)
						.ifPresent(factory -> ACTIVE.set(true));
				}
			}
		);
	}
}
