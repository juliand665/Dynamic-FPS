package dynamicfps.mixin;

import dynamicfps.DynamicFPSMod.WindowHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.*;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements WindowHolder {
	@Shadow
	@Final
	private Window window;
	
	@Override
	public Window getWindow() {
		return window;
	}
}
