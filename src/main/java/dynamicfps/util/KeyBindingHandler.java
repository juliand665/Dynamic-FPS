package dynamicfps.util;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;

public final class KeyBindingHandler implements ClientTickCallback {
	private final FabricKeyBinding keyBinding;
	private boolean isHoldingKey = false;
	private PressHandler pressHandler;
	
	public KeyBindingHandler(FabricKeyBinding keyBinding, PressHandler pressHandler) {
		this.keyBinding = keyBinding;
		this.pressHandler = pressHandler;
	}
	
	@Override
	public final void tick(MinecraftClient e) {
		if (keyBinding.isPressed()) {
			if (!isHoldingKey) {
				pressHandler.handlePress();
			}
			isHoldingKey = true;
		} else {
			isHoldingKey = false;
		}
	}
	
	@FunctionalInterface
	public interface PressHandler {
		void handlePress();
	}
}
