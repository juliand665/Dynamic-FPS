package dynamic_fps.impl.service;

public interface ModCompat {
	boolean isDisabled();

	boolean disableOverlayOptimization();
	boolean isScreenOptedIn(String className);
	boolean isScreenOptedOut(String className);

	static ModCompat getInstance() {
		return Services.MOD_COMPAT;
	}
}
