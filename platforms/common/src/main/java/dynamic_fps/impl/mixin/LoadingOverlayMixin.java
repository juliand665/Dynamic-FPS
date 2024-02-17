package dynamic_fps.impl.mixin;

import net.minecraft.client.gui.screens.LoadingOverlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dynamic_fps.impl.util.duck.DuckLoadingOverlay;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin implements DuckLoadingOverlay {
	/*
	@Shadow
	private long fadeOutStart;
	 */

	@Override
	public boolean dynamic_fps$isReloadComplete() {
		LoadingOverlay self = (LoadingOverlay)(Object) this;
		return self.fadeOutStart > -1L;
	}
}
