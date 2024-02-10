package dynamic_fps.impl.service;

import java.nio.file.Path;

public interface Platform {
	Path getCacheDir();
	Path getConfigDir();
	boolean isDevelopmentEnvironment();

	void registerStartTickEvent(StartTickEvent event);

	static Platform getInstance() {
		return Services.PLATFORM;
	}

	@FunctionalInterface
	interface StartTickEvent {
		void onStartTick();
	}
}
