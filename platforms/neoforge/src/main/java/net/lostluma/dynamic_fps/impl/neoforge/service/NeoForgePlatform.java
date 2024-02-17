package net.lostluma.dynamic_fps.impl.neoforge.service;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.service.Platform;
import net.neoforged.bus.EventBus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeEventHandler;
import net.neoforged.neoforge.event.TickEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class NeoForgePlatform implements Platform {
	@Override
	public String getName() {
		return "NeoForge";
	}

	@Override
	public String modVersion() {
		Optional<? extends ModContainer> optional = ModList.get().getModContainerById(Constants.MOD_ID);

		if (optional.isPresent()) {
			return optional.get().getModInfo().getVersion().toString();
		} else {
			throw new RuntimeException("Own mod container is somehow not available!");
		}
	}

	@Override
	public Path getCacheDir() {
		Path base = FMLPaths.GAMEDIR.get();
		return this.ensureDir(base.resolve(".cache").resolve(Constants.MOD_ID));
	}

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public void registerStartTickEvent(StartTickEvent event) {
		NeoForge.EVENT_BUS.addListener(TickEvent.ClientTickEvent.class, (unused) -> event.onStartTick());
	}

	private Path ensureDir(Path path) {
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
            throw new RuntimeException("Failed to create Dynamic FPS directory.", e);
        }

		return path;
    }
}
