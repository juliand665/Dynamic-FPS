package dynamic_fps.impl.service;

import java.util.Set;

public interface ModCompat {
	boolean isDisabled();

	boolean disableOverlayOptimization();

	static ModCompat getInstance() {
		return Services.MOD_COMPAT;
	}
}
