package net.lostluma.dynamic_fps.impl.quilt.service;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.service.Platform;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class QuiltPlatform implements Platform {
	@Override
	public String getName() {
		return "Quilt";
	}

	@Override
	public String modVersion() {
		Optional<ModContainer> optional = QuiltLoader.getModContainer(Constants.MOD_ID);

		if (optional.isPresent()) {
			return optional.get().metadata().version().toString();
		} else {
			throw new RuntimeException("Own mod container is somehow not available!");
		}
	}

	@Override
	public Path getCacheDir() {
		return this.ensureDir(QuiltLoader.getCacheDir().resolve(Constants.MOD_ID));
	}

	@Override
	public Path getConfigDir() {
		return QuiltLoader.getConfigDir();
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return QuiltLoader.isDevelopmentEnvironment();
	}

	@Override
	public void registerStartTickEvent(StartTickEvent event) {
		ClientTickEvents.START.register((minecraft) -> event.onStartTick());
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
