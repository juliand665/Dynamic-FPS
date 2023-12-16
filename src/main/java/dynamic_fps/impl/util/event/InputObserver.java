package dynamic_fps.impl.util.event;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import net.minecraft.Util;

public class InputObserver {
	private final long window;

	private long lastAction = Util.getEpochMillis();

	// Keyboard
	private final GLFWKeyCallback previousKeyCallback;
	private final GLFWCharModsCallback previousCharModsCallback;

	// Mouse / Trackpad etc.
	private final GLFWDropCallback previousDropCallback;
	private final GLFWScrollCallback previousScrollCallback;
	private final GLFWCursorPosCallback previousCursorPosCallback;
	private final GLFWMouseButtonCallback previousMouseClickCallback;

	public InputObserver(long address) {
		this.window = address;

		this.previousKeyCallback = GLFW.glfwSetKeyCallback(this.window, this::onKey);
		this.previousCharModsCallback = GLFW.glfwSetCharModsCallback(this.window, this::onCharMods);

		this.previousDropCallback = GLFW.glfwSetDropCallback(this.window, this::onDrop);
		this.previousScrollCallback = GLFW.glfwSetScrollCallback(this.window, this::onScroll);
		this.previousCursorPosCallback = GLFW.glfwSetCursorPosCallback(this.window, this::onMove);
		this.previousMouseClickCallback = GLFW.glfwSetMouseButtonCallback(this.window, this::onPress);
	}

	public long lastActionTime() {
		return this.lastAction;
	}

	private void updateTime() {
		this.lastAction = Util.getEpochMillis();
	}

	// Keyboard events

	private void onKey(long address, int key, int scancode, int action, int mods) {
		this.updateTime();

		if (this.previousKeyCallback != null) {
			this.previousKeyCallback.invoke(address, key, scancode, action, mods);
		}
	}

	private void onCharMods(long address, int codepoint, int mods) {
		this.updateTime();

		if (this.previousCharModsCallback != null) {
			this.previousCharModsCallback.invoke(address, codepoint, mods);
		}
	}

	// Mouse events

	private void onDrop(long address, int count, long names) {
		this.updateTime();

		if (this.previousDropCallback != null) {
			this.previousDropCallback.invoke(address, count, names);
		}
	}

	private void onScroll(long address, double xoffset, double yoffset) {
		this.updateTime();

		if (this.previousScrollCallback != null) {
			this.previousScrollCallback.invoke(address, xoffset, yoffset);
		}
	}

	private void onMove(long address, double x, double y) {
		this.updateTime();

		if (this.previousCursorPosCallback != null) {
			this.previousCursorPosCallback.invoke(address, x, y);
		}
	}

	private void onPress(long address, int button, int action, int mods) {
		this.updateTime();

		if (this.previousMouseClickCallback != null) {
			this.previousMouseClickCallback.invoke(address, button, action, mods);
		}
	}
}
