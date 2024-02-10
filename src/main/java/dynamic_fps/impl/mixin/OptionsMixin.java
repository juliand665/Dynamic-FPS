package dynamic_fps.impl.mixin;

import dynamic_fps.impl.util.KeyMappingHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
@Pseudo
public class OptionsMixin {
	@Shadow
	@Final
	@Mutable
	public KeyMapping[] keyMappings;

	/**
	 * Add the mod's key mappings to the vanilla options screen.
	 */
	@Inject(method = "load", at = @At("HEAD"))
	private void load(CallbackInfo callbackInfo) {
		this.keyMappings = KeyMappingHandler.register(this.keyMappings);
	}
}
