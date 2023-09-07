package dynamic_fps.impl.util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;

import dynamic_fps.impl.DynamicFPSMod;

public class WindowObserver {
	private final long window;

	private boolean isFocused = true;
	private final GLFWWindowFocusCallback previousFocusCallback;

	private boolean isHovered = true;
	private final GLFWCursorEnterCallback previousMouseCallback;

	private boolean isIconified = false;
	private final GLFWWindowIconifyCallback previousIconifyCallback;

	public WindowObserver(long address) {
		this.window = address;

		previousFocusCallback = GLFW.glfwSetWindowFocusCallback(this.window, this::onFocusChanged);
		previousMouseCallback = GLFW.glfwSetCursorEnterCallback(this.window, this::onMouseChanged);

		// Vanilla doesn't use this (currently), other mods might register this callback though ...
		previousIconifyCallback = GLFW.glfwSetWindowIconifyCallback(this.window, this::onIconifyChanged);
	}

	private boolean isCurrentWindow(long address) {
		return address == this.window;
	}

	public boolean isFocused() {
		return this.isFocused;
	}

	private void onFocusChanged(long address, boolean focused) {
		if (this.isCurrentWindow(address)) {
			this.isFocused = focused;
			DynamicFPSMod.onStatusChanged();
		}

		if (previousFocusCallback != null) {
			previousFocusCallback.invoke(address, focused);
		}
	}

	public boolean isHovered() {
		return this.isHovered;
	}

	private void onMouseChanged(long address, boolean hovered) {
		if (this.isCurrentWindow(address)) {
			this.isHovered = hovered;
			DynamicFPSMod.onStatusChanged();
		}

		if (previousMouseCallback != null) {
			previousMouseCallback.invoke(address, hovered);
		}
	}

	public boolean isIconified() {
		return this.isIconified;
	}

	private void onIconifyChanged(long address, boolean iconified) {
		if (this.isCurrentWindow(address)) {
			this.isIconified = iconified;
			DynamicFPSMod.onStatusChanged();
		}

		if (previousIconifyCallback != null) {
			previousIconifyCallback.invoke(address, iconified);
		}
	}
}
