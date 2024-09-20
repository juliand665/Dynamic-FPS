package dynamic_fps.impl.mixin;

import dynamic_fps.impl.util.duck.DuckScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.achievement.StatsScreen;

@Mixin(StatsScreen.class)
public class StatsScreenMixin {
	@Inject(method = "onStatsUpdated", at = @At("HEAD"))
	private void onStatsUpdated(CallbackInfo callbackInfo) {
		((DuckScreen) this).dynamic_fps$setRendersBackground();
	}
}
