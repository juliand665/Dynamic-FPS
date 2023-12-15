package dynamic_fps.impl.mixin;

import net.minecraft.client.gui.screens.LoadingOverlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dynamic_fps.impl.util.duck.DuckSplashOverlay;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin implements DuckSplashOverlay {
	@Shadow
	private long fadeOutStart;

	@Override
	public boolean dynamic_fps$isReloadComplete() {
		return this.fadeOutStart > -1L;
	}
}
