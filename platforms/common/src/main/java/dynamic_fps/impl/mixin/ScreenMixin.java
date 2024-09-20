package dynamic_fps.impl.mixin;

import dynamic_fps.impl.service.ModCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamic_fps.impl.util.duck.DuckScreen;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Screen.class)
public class ScreenMixin implements DuckScreen {
	@Unique
	private boolean dynamic_fps$canOptimize = false;

	@Unique
	private boolean dynamic_fps$hasOptedOut = false;

	@Override
	public boolean dynamic_fps$rendersBackground() {
		return dynamic_fps$canOptimize;
	}

	@Override
	public void dynamic_fps$setRendersBackground() {
		this.dynamic_fps$canOptimize = true;
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void onInit(CallbackInfo callbackInfo) {
		String name = this.getClass().getName();

		this.dynamic_fps$hasOptedOut = ModCompat.getInstance().isScreenOptedOut(name);

		// Allow other mods to opt out on behalf of vanilla screens
		// That Dynamic FPS forced to opt in via its own mod metadata.
		if (!this.dynamic_fps$hasOptedOut) {
			this.dynamic_fps$canOptimize = ModCompat.getInstance().isScreenOptedIn(name);
		}
	}

	@Inject(method = "renderDirtBackground", at = @At("HEAD"))
	private void onRenderDirtBackground(CallbackInfo callbackInfo) {
		if (!this.dynamic_fps$hasOptedOut) {
			this.dynamic_fps$canOptimize = true; // Signal to apply optimizations on next frame
		}
	}
}
