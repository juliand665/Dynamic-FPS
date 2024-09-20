package dynamic_fps.impl.feature.state;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;

import dynamic_fps.impl.DynamicFPSMod;

public class WindowObserver {
	private final long address;

	private boolean isFocused = true;
	private final GLFWWindowFocusCallback previousFocusCallback;

	private boolean isHovered = true;
	private final GLFWCursorEnterCallback previousMouseCallback;

	private boolean isIconified = false;
	private final GLFWWindowIconifyCallback previousIconifyCallback;

	public WindowObserver(long address) {
		this.address = address;

		this.previousFocusCallback = GLFW.glfwSetWindowFocusCallback(this.address, this::onFocusChanged);
		this.previousMouseCallback = GLFW.glfwSetCursorEnterCallback(this.address, this::onMouseChanged);

		// Vanilla doesn't use this (currently), other mods might register this callback though ...
		this.previousIconifyCallback = GLFW.glfwSetWindowIconifyCallback(this.address, this::onIconifyChanged);
	}

	private boolean isCurrentWindow(long address) {
		return address == this.address;
	}

	public long address() {
		return this.address;
	}

	public boolean isFocused() {
		return this.isFocused;
	}

	private void onFocusChanged(long address, boolean focused) {
		if (this.isCurrentWindow(address)) {
			this.isFocused = focused;
			DynamicFPSMod.onStatusChanged(true);
		}

		if (this.previousFocusCallback != null) {
			this.previousFocusCallback.invoke(address, focused);
		}
	}

	public boolean isHovered() {
		return this.isHovered;
	}

	private void onMouseChanged(long address, boolean hovered) {
		if (this.isCurrentWindow(address)) {
			this.isHovered = hovered;
			DynamicFPSMod.onStatusChanged(true);
		}

		if (this.previousMouseCallback != null) {
			this.previousMouseCallback.invoke(address, hovered);
		}
	}

	public boolean isIconified() {
		return this.isIconified;
	}

	private void onIconifyChanged(long address, boolean iconified) {
		if (this.isCurrentWindow(address)) {
			this.isIconified = iconified;
			DynamicFPSMod.onStatusChanged(true);
		}

		if (this.previousIconifyCallback != null) {
			this.previousIconifyCallback.invoke(address, iconified);
		}
	}
}
