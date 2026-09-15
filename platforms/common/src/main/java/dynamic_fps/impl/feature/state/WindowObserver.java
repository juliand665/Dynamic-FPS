package dynamic_fps.impl.feature.state;

import dynamic_fps.impl.DynamicFPSMod;

import org.lwjgl.sdl.SDLEvents;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_Event;

public class WindowObserver {
	private boolean isFocused;
	private boolean isHovered;
	private boolean isIconified;

	public WindowObserver(long address) {
		long flags = SDLVideo.SDL_GetWindowFlags(address);

		this.isFocused = (flags & SDLVideo.SDL_WINDOW_INPUT_FOCUS) != 0;
		this.isHovered = (flags & SDLVideo.SDL_WINDOW_MOUSE_FOCUS) != 0;
		this.isIconified = (flags & SDLVideo.SDL_WINDOW_MINIMIZED) != 0 || (flags & SDLVideo.SDL_WINDOW_OCCLUDED) != 0;
	}

	public boolean isFocused() {
		return this.isFocused;
	}

	public boolean isHovered() {
		return this.isHovered;
	}

	public boolean isIconified() {
		return this.isIconified;
	}

	public void onEvent(final SDL_Event event) {
		switch (event.type()) {
			case SDLEvents.SDL_EVENT_WINDOW_FOCUS_GAINED -> {
				this.isFocused = true;
				DynamicFPSMod.onStatusChanged(true);

				ClickIgnoreHandler handler = DynamicFPSMod.getClickHandler();

				if (handler != null) {
					handler.onFocused();
				}
			}
			case SDLEvents.SDL_EVENT_WINDOW_FOCUS_LOST -> {
				this.isFocused = false;
				DynamicFPSMod.onStatusChanged(true);
			}
			case SDLEvents.SDL_EVENT_WINDOW_MOUSE_ENTER -> {
				this.isHovered = true;
				DynamicFPSMod.onStatusChanged(true);
			}
			case SDLEvents.SDL_EVENT_WINDOW_MOUSE_LEAVE -> {
				this.isHovered = false;
				DynamicFPSMod.onStatusChanged(true);
			}
			case SDLEvents.SDL_EVENT_WINDOW_OCCLUDED, SDLEvents.SDL_EVENT_WINDOW_MINIMIZED, SDLEvents.SDL_EVENT_WINDOW_HIDDEN -> {
				this.isIconified = true;
				DynamicFPSMod.onStatusChanged(true);
			}
			case SDLEvents.SDL_EVENT_WINDOW_EXPOSED, SDLEvents.SDL_EVENT_WINDOW_RESTORED, SDLEvents.SDL_EVENT_WINDOW_SHOWN -> {
				this.isIconified = false;
				DynamicFPSMod.onStatusChanged(true);
			}
			case SDLEvents.SDL_EVENT_MOUSE_BUTTON_DOWN, SDLEvents.SDL_EVENT_MOUSE_MOTION, SDLEvents.SDL_EVENT_MOUSE_WHEEL,
				 SDLEvents.SDL_EVENT_JOYSTICK_AXIS_MOTION, SDLEvents.SDL_EVENT_JOYSTICK_BALL_MOTION, SDLEvents.SDL_EVENT_JOYSTICK_BUTTON_DOWN,
				 SDLEvents.SDL_EVENT_GAMEPAD_AXIS_MOTION, SDLEvents.SDL_EVENT_GAMEPAD_BUTTON_DOWN, SDLEvents.SDL_EVENT_GAMEPAD_TOUCHPAD_DOWN, SDLEvents.SDL_EVENT_GAMEPAD_TOUCHPAD_MOTION -> {
				IdleHandler.onActivity();
			}
		}
	}
}
