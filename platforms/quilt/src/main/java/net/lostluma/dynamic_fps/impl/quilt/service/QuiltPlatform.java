package net.lostluma.dynamic_fps.impl.quilt.service;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.service.Platform;
import dynamic_fps.impl.util.Version;
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
	public boolean isModLoaded(String modId) {
		return QuiltLoader.isModLoaded(modId);
	}

	@Override
	public Optional<Version> getModVersion(String modId) {
		Optional<ModContainer> optional = QuiltLoader.getModContainer(modId);

		if (!optional.isPresent()) {
			return Optional.empty();
		}

		String raw = optional.get().metadata().version().toString();

		try {
			return Optional.of(Version.of(raw));
		} catch (Version.VersionParseException e) {
			throw new RuntimeException(e);
		}
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
