package dynamic_fps.impl.mixin;

import dynamic_fps.impl.util.DynamicFPSSplashOverlay;
import net.minecraft.client.gui.screens.LoadingOverlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin implements DynamicFPSSplashOverlay {
	@Shadow
	private long fadeOutStart;

	@Override
	public boolean dynamic_fps$isReloadComplete() {
		return fadeOutStart > -1L;
	}
}
