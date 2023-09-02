package dynamic_fps.impl.mixin;

import java.util.List;
import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.PowerState;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	/*
	 * Insert information about effective frame rate below misleading FPS counter.
	 */
	@Inject(method = "getGameInformation", at = @At("RETURN"))
	private void onGetGameInformation(CallbackInfoReturnable<List<String>> callbackInfo) {
		var status = DynamicFPSMod.powerState();

		if (status != PowerState.FOCUSED) {
			var result = callbackInfo.getReturnValue();
			int target = DynamicFPSMod.targetFrameRate();

			result.add(2, String.format(Locale.ROOT, "§c[Dynamic FPS] FPS: %s P: %s§r", target, status.toString().toLowerCase()));
		}
	}
}
