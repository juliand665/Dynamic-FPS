package dynamic_fps.impl.mixin;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.option.GraphicsState;
import dynamic_fps.impl.feature.state.OptionHolder;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Resets runtime modified graphics settings to the user-specified defaults during saving.
 * Prevents the game from saving the reduced or minimal preset to disk and loading it again.
 */
@Mixin(Options.class)
public abstract class OptionsMixin {
	@Inject(method = "save", at = @At("HEAD"))
	private void save0(CallbackInfo callbackInfo) {
		if (DynamicFPSMod.graphicsState() != GraphicsState.DEFAULT) {
			OptionHolder.applyOptions(Minecraft.getInstance().options, GraphicsState.DEFAULT);
		}
	}

	@Inject(method = "save", at = @At("RETURN"))
	private void save1(CallbackInfo callbackInfo) {
		if (DynamicFPSMod.graphicsState() != GraphicsState.DEFAULT) {
			OptionHolder.applyOptions(Minecraft.getInstance().options, DynamicFPSMod.graphicsState());
		}
	}
}
