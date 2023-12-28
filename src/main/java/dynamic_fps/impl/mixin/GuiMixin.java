package dynamic_fps.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dynamic_fps.impl.DynamicFPSMod;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class GuiMixin {
	/**
	 * Cancels rendering the GUI if a it is determined to currently not be visible.
	 */
	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void shouldRender(CallbackInfo callbackInfo) {
		if (!DynamicFPSMod.shouldShowLevels()) {
			callbackInfo.cancel();
		}
	}
}
