package dynamic_fps.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class FallbackConfigScreen extends Screen {
	private final Screen parent;

	private static final Component WARNING_0 = Component.translatable("config.dynamic_fps.warn_cloth_config.0");
	private static final Component WARNING_1 = Component.translatable("config.dynamic_fps.warn_cloth_config.1");

	public FallbackConfigScreen(Screen parent) {
		super(Component.translatable("config.dynamic_fps.title"));

		this.parent = parent;
	}

	@Override
	protected void init() {
		var width = 152;
		var height = 20;
		var x = (this.width - width) / 2;
		var y = this.height - height - 5;

		this.addRenderableWidget(
			Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).bounds(x, y, width, height).build()
		);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		var width = guiGraphics.guiWidth() / 2;
		var height = guiGraphics.guiHeight() / 3;

		guiGraphics.drawCenteredString(this.font, WARNING_0.getVisualOrderText(), width, height, 0xFFFFFF);
		guiGraphics.drawCenteredString(this.font, WARNING_1.getVisualOrderText(), width, height + 10, 0xFFFFFF);
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(this.parent);
	}
}
