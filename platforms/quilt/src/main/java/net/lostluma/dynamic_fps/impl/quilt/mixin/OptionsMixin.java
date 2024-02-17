package net.lostluma.dynamic_fps.impl.quilt.mixin;

import com.google.common.collect.Lists;
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

import java.util.List;

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
		List<KeyMapping> mappings = Lists.newArrayList(this.keyMappings);

		for (KeyMappingHandler handler : KeyMappingHandler.getHandlers()) {
			mappings.add(handler.keyMapping());
		}

		this.keyMappings = mappings.toArray(new KeyMapping[0]);
	}
}
