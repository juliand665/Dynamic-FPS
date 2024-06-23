package dynamic_fps.impl;

import dynamic_fps.impl.service.Platform;

public class Constants {
	// Meta
	public static final String MOD_ID = "dynamic_fps";
	public static final boolean DEBUG = Platform.getInstance().isDevelopmentEnvironment();

	// Miscellaneous
	// Minecraft considers limits >=260 as infinite
	public static final int NO_FRAME_RATE_LIMIT = 260;

	// The Cloth Config mod ID has changed a few times
	public static final String[] CLOTH_CONFIG_ID = new String[] { "cloth-config", "cloth_config", "cloth-config2" };
}
