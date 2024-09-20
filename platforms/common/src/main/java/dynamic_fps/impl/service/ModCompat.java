package dynamic_fps.impl.service;

import java.util.Set;

public interface ModCompat {
	boolean isDisabled();

	boolean disableOverlayOptimization();

	Set<String> getOptedInScreens();
	Set<String> getOptedOutScreens();

	default boolean isScreenOptedIn(String className) {
		return getOptedInScreens().contains(className);
	}

	default boolean isScreenOptedOut(String className) {
		return getOptedOutScreens().contains(className);
	}

	static ModCompat getInstance() {
		return Services.MOD_COMPAT;
	}
}
