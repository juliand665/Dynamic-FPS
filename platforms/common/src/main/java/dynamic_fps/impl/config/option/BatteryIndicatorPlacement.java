package dynamic_fps.impl.config.option;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Screen corner to render the battery indicator in.
 */
public enum BatteryIndicatorPlacement {
	TOP_LEFT(graphics -> new int[] {4, 4}),
	TOP_RIGHT(graphics -> new int[] {graphics.guiWidth() - 47, 4}),
	BOTTOM_LEFT(graphics -> new int[] {4, graphics.guiHeight() - 20}),
	BOTTOM_RIGHT(graphics -> new int[] {graphics.guiWidth() - 47, graphics.guiHeight() - 20});

	private final DynamicPlacement placement;

	BatteryIndicatorPlacement(DynamicPlacement placement) {
		this.placement = placement;
	}

	public int[] get(GuiGraphics graphics) {
		return this.placement.get(graphics);
	}

	@FunctionalInterface
	private interface DynamicPlacement {
		int[] get(GuiGraphics graphics);
	}
}
