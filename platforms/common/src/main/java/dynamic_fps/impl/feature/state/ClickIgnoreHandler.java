package dynamic_fps.impl.feature.state;

import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.config.option.IgnoreInitialClick;
import net.minecraft.client.Minecraft;

import java.time.Instant;

public class ClickIgnoreHandler {
	private long focusedAt;

	public ClickIgnoreHandler() {}

	public static boolean isFeatureActive() {
		return DynamicFPSConfig.INSTANCE.ignoreInitialClick() != IgnoreInitialClick.DISABLED;
	}

	public boolean shouldIgnoreClick() {
		Minecraft minecraft = Minecraft.getInstance();
		IgnoreInitialClick config = DynamicFPSConfig.INSTANCE.ignoreInitialClick();

		if (config == IgnoreInitialClick.DISABLED) {
			return false;
		}

		if (config == IgnoreInitialClick.IN_WORLD && minecraft.gui.screen() != null) {
			return false;
		}

		return this.focusedAt + 10 >= Instant.now().toEpochMilli();
	}

	public void onFocused() {
		this.focusedAt = Instant.now().toEpochMilli();
	}
}
