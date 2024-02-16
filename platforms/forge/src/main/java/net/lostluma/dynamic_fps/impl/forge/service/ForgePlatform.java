package net.lostluma.dynamic_fps.impl.forge.service;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.service.Platform;
import net.lostluma.dynamic_fps.impl.forge.DynamicFPSForgeMod;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

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
