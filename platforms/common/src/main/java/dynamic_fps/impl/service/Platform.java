package dynamic_fps.impl.service;

import java.nio.file.Path;
import java.util.Optional;

public interface Platform {
	String getName();

	Path getCacheDir();
	Path getConfigDir();
	boolean isDevelopmentEnvironment();

	Optional<String> getModVersion(String modId);

	void registerStartTickEvent(StartTickEvent event);

	@FunctionalInterface
	interface StartTickEvent {
		void onStartTick();
	}

	static Platform getInstance() {
		return Services.PLATFORM;
	}
}
