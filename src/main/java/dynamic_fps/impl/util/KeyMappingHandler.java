package dynamic_fps.impl.util;

import com.google.common.collect.Lists;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.service.Platform;
import net.minecraft.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;

import java.util.List;

import static dynamic_fps.impl.util.Localization.translationKey;

public final class KeyMappingHandler {
	private final KeyMapping keyMapping;
	private boolean isHoldingKey = false;
	private final PressHandler pressHandler;

	private static final KeyMappingHandler[] KEY_MAPPING_HANDLERS = {
		new KeyMappingHandler(
			translationKey("key", "toggle_forced"),
			"key.categories.misc",
			DynamicFPSMod::toggleForceLowFPS
		),
		new KeyMappingHandler(
			translationKey("key", "toggle_disabled"),
			"key.categories.misc",
			DynamicFPSMod::toggleDisabled
		)
	};

	public KeyMappingHandler(String translationKey, String category, PressHandler pressHandler) {
		this.keyMapping = new KeyMapping(
			translationKey,
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			category
		);
		this.pressHandler = pressHandler;
	}

	public static KeyMapping[] register(KeyMapping[] existing) {
		List<KeyMapping> mappings = Lists.newArrayList(existing);

		for (KeyMappingHandler handler : KEY_MAPPING_HANDLERS ) {
			mappings.add(handler.keyMapping);
			Platform.getInstance().registerStartTickEvent(handler::onStartTick);
		}

		return mappings.toArray(new KeyMapping[0]);
	}

	public void onStartTick() {
		if (this.keyMapping.isDown()) {
			if (!this.isHoldingKey) {
				this.pressHandler.handlePress();
			}
			this.isHoldingKey = true;
		} else {
			this.isHoldingKey = false;
		}
	}

	@FunctionalInterface
	public interface PressHandler {
		void handlePress();
	}
}
