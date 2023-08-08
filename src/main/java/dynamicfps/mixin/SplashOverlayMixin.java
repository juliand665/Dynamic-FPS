package dynamicfps.mixin;

import dynamicfps.util.DynamicFPSSplashOverlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin implements DynamicFPSSplashOverlay {
	@Shadow
	private long reloadCompleteTime;

	@Override
	public boolean dynamicfps$isReloadComplete() {
		return reloadCompleteTime > -1L;
	}
}
