package dynamicfps.mixin;

import dynamicfps.util.DynamicFPSSplashOverlay;
import net.minecraft.client.gui.screens.LoadingOverlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin implements DynamicFPSSplashOverlay {
	@Shadow
	private long fadeOutStart;

	@Override
	public boolean dynamicfps$isReloadComplete() {
		return fadeOutStart > -1L;
	}
}
