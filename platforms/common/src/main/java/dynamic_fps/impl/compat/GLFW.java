package dynamic_fps.impl.compat;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.util.Version;
import net.minecraft.client.Minecraft;

public class GLFW {
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static final boolean enterEventBroken = isEnterEventBroken();

	/**
	 * Apply a workaround for the cursor enter event not working if needed.
	 *
	 * This fixes an issue when running GLFW version 3.3.x or earlier where
	 * the cursor enter event will only work if the window is not capturing
	 * The mouse cursor. Since this is often not the case when switching windows
	 * Dynamic FPS releases and captures the cursor in tandem with window focus.
	 */
	public static void applyWorkaround() {
		if (!useWorkaround()) {
			return;
		}

		if (DynamicFPSMod.getWindow() == null) {
			return;
		}

		if (!DynamicFPSMod.getWindow().isFocused()) {
			minecraft.mouseHandler.releaseMouse();
		} else {
			// Grabbing the mouse only works while Minecraft
			// Agrees that the window is focused. The mod is
			// A little too fast for this, so we schedule it
			// For the next client tick (before next frame).
			minecraft.schedule(minecraft.mouseHandler::grabMouse);
		}
	}

	private static boolean useWorkaround() {
		return enterEventBroken && minecraft.screen == null && !minecraft.options.pauseOnLostFocus;
	}

	private static boolean isEnterEventBroken() {
		Version active = getGLFWVersion();
		return active.compareTo(Version.of(3, 3, 0)) < 0; // Versions before 3.3.0 are broken
	}

	private static Version getGLFWVersion() {
		int[] major = new int[1];
		int[] minor = new int[1];
		int[] patch = new int[1];

		org.lwjgl.glfw.GLFW.glfwGetVersion(major, minor, patch);
		return Version.of(major[0], minor[0], patch[0]);
	}
}
