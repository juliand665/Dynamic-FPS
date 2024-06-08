package dynamic_fps.impl.mixin;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.PowerState;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	/**
	 * Show the current power state and effective frame rate below the FPS counter, unless focused.
	 * <p>
	 * As we only slow the client loop to a minimum of 15 TPS the vanilla frame rate counter is inaccurate and confusing.
	 */
	@Inject(method = "getGameInformation", at = @At("RETURN"), cancellable = true)
	private void getGameInformation(CallbackInfoReturnable<List<String>> cir) {
		List<String> result = cir.getReturnValue();
		if (DynamicFPSMod.isDisabled()) {
			String reason = DynamicFPSMod.whyIsTheModNotWorking();
			result.add(2, this.format("§c[Dynamic FPS] Inactive! Reason: %s§r", reason));
		} else {
			PowerState status = DynamicFPSMod.powerState();

			if (status != PowerState.FOCUSED) {
				int target = DynamicFPSMod.targetFrameRate();
				result.add(
					2,
					this.format("§c[Dynamic FPS] FPS: %s P: %s§r", target, status.toString().toLowerCase())
				);
			}
		}

		cir.setReturnValue(result);
	}

	@Unique
	private String format(String template, Object... args) {
		return String.format(Locale.ROOT, template, args);
	}
}
