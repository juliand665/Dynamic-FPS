package dynamicfps.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamicfps.util.DynamicFPSScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public class ScreenMixin implements DynamicFPSScreen {
	@Unique
	private boolean dynamicfps$rendersBackground = false;

	@Override
	public boolean dynamicfps$rendersBackground() {
		return dynamicfps$rendersBackground;
	}

	@Inject(method = "renderBackgroundTexture", at = @At("HEAD"))
	private void onRenderBackgroundTexture(CallbackInfo callbackInfo) {
		// This screen is not compatible with this optimization,
		// As it waits for chunks to be done rendering to close.
		if ((Screen)(Object)this instanceof DownloadingTerrainScreen) {
			return;
		}

		this.dynamicfps$rendersBackground = true; // Signal to apply optimizations on next frame
	}
}
