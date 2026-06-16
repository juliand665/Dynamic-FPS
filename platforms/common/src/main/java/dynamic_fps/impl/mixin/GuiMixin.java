package dynamic_fps.impl.mixin;

import dynamic_fps.impl.feature.state.IdleHandler;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "setScreen", at = @At("HEAD"))
	private void setScreen(CallbackInfo callbackInfo) {
		IdleHandler.onActivity();
	}
}
