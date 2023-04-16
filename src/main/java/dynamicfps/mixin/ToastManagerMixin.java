package dynamicfps.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamicfps.DynamicFPSMod;
import net.minecraft.client.toast.ToastManager;

@Mixin(ToastManager.class)
public class ToastManagerMixin {
    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void onDraw(CallbackInfo callbackInfo) {
        if (!DynamicFPSMod.shouldShowToasts()) {
			callbackInfo.cancel();
		}
    }
}
