package net.lostluma.dynamic_fps.impl.neoforge.mixin;

import dynamic_fps.impl.util.duck.DuckScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsScreen.class)
public class StatsScreenMixin {
	@Inject(method = "onStatsUpdated", at = @At("HEAD"))
	private void onStatsUpdated(CallbackInfo callbackInfo) {
		((DuckScreen) this).dynamic_fps$setRendersBackground();
	}
}
