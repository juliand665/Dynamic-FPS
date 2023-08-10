package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.gui.components.toasts.ToastComponent;

@Mixin(ToastComponent.class)
public class ToastComponentMixin {
	@Inject(method = "freeSlots", at = @At("HEAD"), cancellable = true)
	private void onFreeSlots(CallbackInfoReturnable<Integer> callbackInfo) {
		if (!DynamicFPSMod.shouldShowToasts()) {
			callbackInfo.setReturnValue(0);
		}
	}
}
