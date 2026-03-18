package dynamic_fps.impl.mixin.bugfix;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugScreenEntryList.class)
public class DebugScreenEntryListMixin {
	@Inject(method = "isCurrentlyEnabled", at = @At("HEAD"), cancellable = true)
	private void isCurrentlyEnabled(Identifier identifier, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (DynamicFPSMod.targetFrameRate() == 0 && DebugScreenEntries.GPU_UTILIZATION.equals(identifier)) {
			callbackInfo.setReturnValue(false);
		}
	}
}
