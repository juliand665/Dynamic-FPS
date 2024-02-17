package dynamic_fps.impl.service;

import java.nio.file.Path;

public interface Platform {
	String getName();
	String modVersion();

	Path getCacheDir();
	Path getConfigDir();
	boolean isDevelopmentEnvironment();

	void registerStartTickEvent(StartTickEvent event);

	@FunctionalInterface
	interface StartTickEvent {
		void onStartTick();
	}

	static Platform getInstance() {
		return Services.PLATFORM;
	}
}
