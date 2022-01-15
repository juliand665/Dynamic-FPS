package dynamicfps.mixin;

import dynamicfps.DynamicFPSMod.SplashOverlayAccessor;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin implements SplashOverlayAccessor {
	@Shadow
	private long reloadCompleteTime;
	
	@Override
	public boolean isReloadComplete() {
		return reloadCompleteTime > -1L;
	}
}
