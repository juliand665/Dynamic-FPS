package net.lostluma.dynamic_fps.impl.neoforge.mixin;

import dynamic_fps.impl.util.duck.DuckLoadingOverlay;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin implements DuckLoadingOverlay {
	@Shadow
	private long fadeOutStart;

	@Override
	public boolean dynamic_fps$isReloadComplete() {
		return this.fadeOutStart > -1L;
	}
}
