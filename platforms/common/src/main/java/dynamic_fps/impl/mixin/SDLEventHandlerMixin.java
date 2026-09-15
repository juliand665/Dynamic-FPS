package dynamic_fps.impl.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.SDLEventHandler;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.feature.state.ClickIgnoreHandler;
import dynamic_fps.impl.feature.state.WindowObserver;
import org.lwjgl.sdl.SDL_Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SDLEventHandler.class)
public class SDLEventHandlerMixin {
	// Inject after SDL_PollEvent instead of wrapping it with ME
	// To not appear in unrelated stacktraces, causing confusion
	@Inject(
		method = "pollEvents",
		at = @At(
			value = "INVOKE",
			target = "Lorg/lwjgl/sdl/SDLEvents;SDL_PollEvent(Lorg/lwjgl/sdl/SDL_Event;)Z",
			shift = At.Shift.AFTER
		)
	)
	private void pollEvents(CallbackInfo callbackInfo, @Local SDL_Event event) {
		WindowObserver handler = DynamicFPSMod.getWindow();

		if (handler != null) {
			handler.onEvent(event);
		}
	}

	@Inject(method = "handleMouseButtonEvent", at = @At("HEAD"), cancellable = true)
	private void handleMouseButtonEvent(SDL_Event event, CallbackInfo callbackInfo) {
		ClickIgnoreHandler handler = DynamicFPSMod.getClickHandler();

		if (handler != null && handler.shouldIgnoreClick()) {
			callbackInfo.cancel();
		}
	}
}
