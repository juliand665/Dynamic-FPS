package dynamicfps.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamicfps.util.DynamicFPSScreen;
import dynamicfps.util.ScreenOptimizationCompat;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Screen.class)
public class ScreenMixin implements DynamicFPSScreen {
	@Unique
	private boolean dynamicfps$canOptimize = false;

	@Unique
	private boolean dynamicfps$hasOptedOut = false;

	@Override
	public boolean dynamicfps$rendersBackground() {
		return dynamicfps$canOptimize;
	}

	@Override
	public void dynamicfps$setRendersBackground(boolean value) {
		this.dynamicfps$canOptimize = value;
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void onInit(CallbackInfo callbackInfo) {
		String name = this.getClass().getName();

		this.dynamicfps$hasOptedOut = ScreenOptimizationCompat.isOptedOut(name);

		// Allow other mods to opt out on behalf of vanilla screens
		// That Dynamic FPS forced to opt in via its own mod metadata.
		if (!this.dynamicfps$hasOptedOut) {
			this.dynamicfps$canOptimize = ScreenOptimizationCompat.isOptedIn(name);
		}
	}

	@Inject(method = "renderDirtBackground", at = @At("HEAD"))
	private void onRenderDirtBackground(CallbackInfo callbackInfo) {
		if (!this.dynamicfps$hasOptedOut) {
			this.dynamicfps$canOptimize = true; // Signal to apply optimizations on next frame
		}
	}
}
