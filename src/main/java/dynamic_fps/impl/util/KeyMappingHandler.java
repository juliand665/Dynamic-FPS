package dynamic_fps.impl.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.mojang.blaze3d.platform.InputConstants;

public final class KeyMappingHandler implements ClientTickEvents.EndTick {
	private final KeyMapping keyMapping;
	private boolean isHoldingKey = false;
	private final PressHandler pressHandler;

	public KeyMappingHandler(String translationKey, String category, PressHandler pressHandler) {
		this(translationKey, InputConstants.UNKNOWN.getValue(), category, pressHandler);
	}

	public KeyMappingHandler(String translationKey, int defaultKeyCode, String category, PressHandler pressHandler) {
		this.keyMapping = new KeyMapping(
			translationKey,
			InputConstants.Type.KEYSYM,
			defaultKeyCode,
			category
		);
		this.pressHandler = pressHandler;
	}

	public void register() {
		KeyBindingHelper.registerKeyBinding(keyMapping);
		ClientTickEvents.END_CLIENT_TICK.register(this);
	}

	@Override
	public final void onEndTick(Minecraft e) {
		if (keyMapping.isDown()) {
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
