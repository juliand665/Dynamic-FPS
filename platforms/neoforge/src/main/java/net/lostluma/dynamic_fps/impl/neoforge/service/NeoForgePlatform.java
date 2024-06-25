package net.lostluma.dynamic_fps.impl.neoforge.service;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.Version;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

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
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public Optional<Version> getModVersion(String modId) {
		Optional<? extends ModContainer> optional = ModList.get().getModContainerById(modId);

		if (!optional.isPresent()) {
			return Optional.empty();
		}

		String raw = optional.get().getModInfo().getVersion().toString();

		try {
			return Optional.of(Version.of(raw));
		} catch (Version.VersionParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void registerStartTickEvent(StartTickEvent event) {
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, (unused) -> event.onStartTick());
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
