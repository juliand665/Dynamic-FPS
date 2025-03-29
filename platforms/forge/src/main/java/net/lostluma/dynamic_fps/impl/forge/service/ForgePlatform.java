package net.lostluma.dynamic_fps.impl.forge.service;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.Version;
import net.lostluma.dynamic_fps.impl.forge.DynamicFPSForgeMod;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ForgePlatform implements Platform {
	@Override
	public String getName() {
		return "Forge";
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
		return LoadingModList.get().getModFileById(modId) != null;
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
		DynamicFPSForgeMod.addTickEventListener(event);
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
