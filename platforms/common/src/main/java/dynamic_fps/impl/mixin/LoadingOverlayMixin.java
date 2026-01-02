package dynamic_fps.impl.mixin;

import net.minecraft.client.gui.screens.LoadingOverlay;

import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dynamic_fps.impl.util.duck.DuckLoadingOverlay;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin implements DuckLoadingOverlay {
	@Shadow
	@Final
	private ReloadInstance reload;

	@Override
	public boolean dynamic_fps$isReloadComplete() {
		return this.reload.isDone();
	}
}
