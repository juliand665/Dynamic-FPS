package dynamic_fps.impl.feature.state;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.config.option.IdleCondition;
import dynamic_fps.impl.feature.battery.BatteryTracker;
import dynamic_fps.impl.service.Platform;
import net.lostluma.battery.api.State;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

public class IdleHandler {
	private static boolean active = false;
	private static boolean wasIdle = false;

	private static long previousActivity = 0L;

	private static Vec3 prevPosition = Vec3.ZERO;
	private static Vec3 prevLookAngle = Vec3.ZERO;

	private static @Nullable GLFWKeyCallback previousKeyCallback;
	private static @Nullable GLFWScrollCallback previousScrollCallback;
	private static @Nullable GLFWCursorPosCallback previousCursorPosCallback;
	private static @Nullable GLFWMouseButtonCallback previousMouseClickCallback;

	public static void init() {
		if (active) {
			return;
		}

		DynamicFPSConfig config = DynamicFPSMod.modConfig;

		if (config.idle().timeout() == 0) {
			return;
		}

		if (config.idle().condition() == IdleCondition.ON_BATTERY && !BatteryTracker.hasBatteries()) {
			return;
		}

		active = true;

		if (DynamicFPSMod.getWindow() != null) {
			setWindow(DynamicFPSMod.getWindow().address());
		}

		Platform.getInstance().registerStartTickEvent(IdleHandler::checkActivity);
	}

	public static void setWindow(long address) {
		if (active) {
			previousKeyCallback = GLFW.glfwSetKeyCallback(address, IdleHandler::onKey);
			previousScrollCallback = GLFW.glfwSetScrollCallback(address, IdleHandler::onScroll);
			previousCursorPosCallback = GLFW.glfwSetCursorPosCallback(address, IdleHandler::onMove);
			previousMouseClickCallback = GLFW.glfwSetMouseButtonCallback(address, IdleHandler::onPress);
		}
	}

	public static void onActivity() {
		previousActivity = Util.getEpochMillis();
	}

	public static boolean isIdle() {
		DynamicFPSConfig config = DynamicFPSMod.modConfig;

		if (config.idle().timeout() == 0) {
			return false;
		}

		if (config.idle().condition() == IdleCondition.ON_BATTERY && !(BatteryTracker.status() == State.DISCHARGING)) {
			return false;
		}

		return (Util.getEpochMillis() - previousActivity) >= (long) config.idle().timeout() * 1000;
	}

	private static void checkActivity() {
		checkPlayerActivity();

		boolean idle = isIdle();

		if (idle != wasIdle) {
			wasIdle = idle;
			DynamicFPSMod.onStatusChanged(!idle);
		}
	}

	private static void checkPlayerActivity() {
		var player = Minecraft.getInstance().player;

		if (player == null) {
			return;
		}

		var position = player.position();
		var lookAngle = player.getLookAngle();

		if (!position.equals(prevPosition) || !lookAngle.equals(prevLookAngle)) {
			onActivity();
		}

		prevPosition = position;
		prevLookAngle = lookAngle;
	}

	// Keyboard events

	private static void onKey(long address, int key, int scancode, int action, int mods) {
		onActivity();

		if (previousKeyCallback != null) {
			previousKeyCallback.invoke(address, key, scancode, action, mods);
		}
	}

	// Mouse events

	private static void onScroll(long address, double xOffset, double yOffset) {
		onActivity();

		if (previousScrollCallback != null) {
			previousScrollCallback.invoke(address, xOffset, yOffset);
		}
	}

	private static void onMove(long address, double x, double y) {
		onActivity();

		if (previousCursorPosCallback != null) {
			previousCursorPosCallback.invoke(address, x, y);
		}
	}

	private static void onPress(long address, int button, int action, int mods) {
		onActivity();

		if (previousMouseClickCallback != null) {
			previousMouseClickCallback.invoke(address, button, action, mods);
		}
	}
}
