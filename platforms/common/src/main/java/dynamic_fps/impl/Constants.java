package dynamic_fps.impl;

import dynamic_fps.impl.service.Platform;

public class Constants {
	// Meta
	public static final String MOD_ID = "dynamic_fps";
	public static final boolean DEBUG = Platform.getInstance().isDevelopmentEnvironment();

	// Miscellaneous

	// Minimum limit, for lower FPS we cancel frames
	public static final int MIN_FRAME_RATE_LIMIT = 15;
	// Minecraft considers limits >=260 as unlimited
	public static final int NO_FRAME_RATE_LIMIT = 260;
	// Default frame rate limit on all title screens
	public static final int TITLE_FRAME_RATE_LIMIT = 60;

	// The Cloth Config mod ID has changed a few times
	public static final String[] CLOTH_CONFIG_ID = new String[] { "cloth-config", "cloth_config", "cloth-config2" };
}
