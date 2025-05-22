package dynamic_fps.impl.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.option.GraphicsState;
import dynamic_fps.impl.feature.state.OptionHolder;
import dynamic_fps.impl.feature.volume.SmoothVolumeHandler;
import net.minecraft.client.*;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
	// Reset runtime modified graphics settings to the user-specified defaults during saving.
	// Prevents the game from saving the reduced or minimal preset to disk and loading it again.
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

	/**
	 * Apply the volume multiplier to any newly-played sounds.
	 */
	@ModifyReturnValue(method = "getSoundSourceVolume", at = @At("RETURN"))
	private float getSoundSourceVolume(float value, @Local(argsOnly = true) @Nullable SoundSource source) {
		return value * SmoothVolumeHandler.volumeMultiplier(source);
	}
}
