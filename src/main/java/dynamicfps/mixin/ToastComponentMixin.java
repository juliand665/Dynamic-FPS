package dynamicfps.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamicfps.DynamicFPSMod;
import net.minecraft.client.gui.components.toasts.ToastComponent;

@Mixin(ToastComponent.class)
public class ToastComponentMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(CallbackInfo callbackInfo) {
        if (!DynamicFPSMod.shouldShowToasts()) {
			callbackInfo.cancel();
		}
    }
}
