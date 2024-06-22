package dynamic_fps.impl.service;

public interface ModCompat {
	boolean isDisabled();

	boolean disableOverlayOptimization();

	static ModCompat getInstance() {
		return Services.MOD_COMPAT;
	}
}
