package dynamic_fps.impl.config.option;

import com.mojang.blaze3d.platform.Window;

/**
 * Screen corner to render the battery indicator in.
 */
public enum BatteryIndicatorPlacement {
	TOP_LEFT(window -> new int[] {4, 4}),
	TOP_RIGHT(window -> new int[] {window.getGuiScaledWidth() - 47, 4}),
	BOTTOM_LEFT(window -> new int[] {4, window.getGuiScaledHeight() - 20}),
	BOTTOM_RIGHT(window -> new int[] {window.getGuiScaledWidth() - 47, window.getGuiScaledHeight() - 20});

	private final DynamicPlacement placement;

	BatteryIndicatorPlacement(DynamicPlacement placement) {
		this.placement = placement;
	}

	public int[] get(Window window) {
		return this.placement.get(window);
	}

	@FunctionalInterface
	private interface DynamicPlacement {
		int[] get(Window window);
	}
}
