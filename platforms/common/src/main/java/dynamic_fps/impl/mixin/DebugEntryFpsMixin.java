package dynamic_fps.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.PowerState;
import net.minecraft.client.gui.components.debug.DebugEntryFps;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Locale;

@Mixin(DebugEntryFps.class)
public class DebugEntryFpsMixin {
	/**
	 * Add extra information to the FPS debug line when not focused.
	 */
	@WrapOperation(
		method = "display",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/debug/DebugScreenDisplayer;addPriorityLine(Ljava/lang/String;)V"
		)
	)
	private void display(DebugScreenDisplayer instance, String line, Operation<Void> original) {
		PowerState state = DynamicFPSMod.powerState();

		if (state != PowerState.FOCUSED) {
			int target = DynamicFPSMod.targetFrameRate();

			StringBuilder extra = new StringBuilder();
			extra.append("§c (");

			// When running below the actual target frame rate
			// Add rT (real Target) with the actual frame rate
			if (target < 15) {
				extra.append("rT: ");
				extra.append(target);
				extra.append(" ");
			}

			// Show which non-default power state is in effect
			extra.append("P: ");
			extra.append(state.toString().toLowerCase(Locale.ROOT));

			extra.append(")§r");
			line += extra.toString();
		}

		original.call(instance, line);
	}
}
