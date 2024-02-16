package dynamic_fps.impl.compat;

import net.minecraft.client.Minecraft;

public class GLFW {
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static final boolean enterEventBroken = isEnterEventBroken();

	/**
	 * Whether to use a workaround for the cursor enter event not working.
	 *
	 * This fixes an issue when running GLFW version 3.3.x or earlier where
	 * the cursor enter event will only work if the window is not capturing
	 * The mouse cursor. Since this is often not the case when switching windows
	 * Dynamic FPS releases and captures the cursor in tandem with window focus.
	 */
	public static boolean useHoverEventWorkaround() {
		return enterEventBroken && minecraft.screen == null && !minecraft.options.pauseOnLostFocus;
	}

	private static boolean isEnterEventBroken() {
		int[] version = getGLFWVersion();
		return !(version[0] > 3 || version[0] == 3 && version[1] > 3);
	}

	private static int[] getGLFWVersion() {
		int[] major = new int[1];
		int[] minor = new int[1];
		int[] patch = new int[1];

		org.lwjgl.glfw.GLFW.glfwGetVersion(major, minor, patch);
		return new int[]{major[0], minor[0], patch[0]}; // This is kinda silly ...
	}
}
