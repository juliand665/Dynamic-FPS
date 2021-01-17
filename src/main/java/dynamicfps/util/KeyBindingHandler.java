package dynamicfps.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class KeyBindingHandler implements ClientTickEvents.EndTick {
	private final KeyBinding keyBinding;
	private boolean isHoldingKey = false;
	private final PressHandler pressHandler;
	
	public KeyBindingHandler(String translationKey, String category, PressHandler pressHandler) {
		this(translationKey, InputUtil.UNKNOWN_KEY.getCode(), category, pressHandler);
	}
	
	public KeyBindingHandler(String translationKey, int defaultKeyCode, String category, PressHandler pressHandler) {
		this.keyBinding = new KeyBinding(
			translationKey,
			InputUtil.Type.KEYSYM,
			defaultKeyCode,
			category
		);
		this.pressHandler = pressHandler;
	}
	
	public void register() {
		KeyBindingHelper.registerKeyBinding(keyBinding);
		ClientTickEvents.END_CLIENT_TICK.register(this);
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
