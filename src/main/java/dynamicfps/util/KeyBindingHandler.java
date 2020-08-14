package dynamicfps.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;

public final class KeyBindingHandler implements ClientTickEvents.EndTick {
	private final KeyBinding keyBinding;
	private boolean isHoldingKey = false;
	private final PressHandler pressHandler;
	
	public KeyBindingHandler(KeyBinding keyBinding, PressHandler pressHandler) {
		this.keyBinding = keyBinding;
		this.pressHandler = pressHandler;
	}
	
	@Override
	public final void onEndTick(MinecraftClient e) {
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
