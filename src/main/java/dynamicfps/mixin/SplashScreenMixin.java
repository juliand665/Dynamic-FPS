package dynamicfps.mixin;

import dynamicfps.DynamicFPSMod.SplashCompletedHolder;
import net.minecraft.client.gui.screen.SplashScreen;
import org.spongepowered.asm.mixin.*;

@Mixin(SplashScreen.class)
public class SplashScreenMixin implements SplashCompletedHolder {
	@Shadow
	private long reloadCompleteTime;

	@Override
	public boolean isReloadComplete() {
		return reloadCompleteTime > -1L;
	}
}
