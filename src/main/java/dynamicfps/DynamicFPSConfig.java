package dynamicfps;

import eu.midnightdust.lib.config.MidnightConfig;

public final class DynamicFPSConfig extends MidnightConfig {
	/// Whether to disable or enable the frame rate drop when unfocused.
	@Entry public static boolean reduceFPSWhenUnfocused = true;
	/// The frame rate to target when unfocused (only applies if `enableUnfocusedFPS` is true).
	@Entry public static int unfocusedFPS = 1;
	/// Whether or not to uncap FPS when hovered, even if it would otherwise be reduced.
	@Entry public static boolean restoreFPSWhenHovered = true;
	/// Whether or not to trigger a garbage collector run whenever the game is unfocused.
	@Entry public static boolean runGCOnUnfocus = false;

	public static boolean isReduceFPSWhenUnfocused() {
		return DynamicFPSConfig.reduceFPSWhenUnfocused;
	}

	public static boolean isRestoreFPSWhenHovered() {
		return DynamicFPSConfig.restoreFPSWhenHovered;
	}

	public static boolean isRunGCOnUnfocus() {
		return DynamicFPSConfig.runGCOnUnfocus;
	}
}
