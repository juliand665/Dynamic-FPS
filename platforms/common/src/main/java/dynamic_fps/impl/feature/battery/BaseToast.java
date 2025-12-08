package dynamic_fps.impl.feature.battery;

import dynamic_fps.impl.util.ResourceLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseToast implements Toast {
	private long firstRender;
	private Visibility visibility;

	protected Component title;
	protected Component description;
	protected @Nullable ResourceLocation icon;

	private static final ResourceLocation MOD_ICON = ResourceLocations.of("dynamic_fps", "textures/battery/toast/background_icon.png");
	private static final ResourceLocation BACKGROUND_IMAGE = ResourceLocations.of("dynamic_fps", "textures/battery/toast/background.png");

	protected BaseToast(Component title, Component description, @Nullable ResourceLocation icon) {
		this.title = title;
		this.description = description;

		this.icon = icon;

		this.visibility = Visibility.SHOW;
	}

	@Override
	public @NotNull Visibility getWantedVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager toastManager, long currentTime) {
		if (this.firstRender == 0) {
			return;
		}

		if (currentTime - this.firstRender >= 5000.0 * toastManager.getNotificationDisplayTimeMultiplier()) {
			this.visibility = Visibility.HIDE;
		}
	}

	@Override
	public void render(GuiGraphics graphics, Font font, long currentTime) {
		if (this.firstRender == 0) {
			this.onFirstRender();
			this.firstRender = currentTime;
		}

		// type, resource, x, y, ?, ?, width, height, width, height
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_IMAGE, 0, 0, 0.0f, 0, this.width(), this.height(), this.width(), this.height());

		int x = 8;

		if (this.icon != null) {
			x += 22;

			graphics.blit(RenderPipelines.GUI_TEXTURED, MOD_ICON, 2, 2, 0.0f, 0, 8, 8, 8, 8);
			graphics.blit(RenderPipelines.GUI_TEXTURED, this.icon, 8, 8, 0.0f, 0, 16, 16, 16, 16);
		}

		graphics.drawString(Minecraft.getInstance().font, this.title, x, 7, 0xff5f3315, false);
		graphics.drawString(Minecraft.getInstance().font, this.description, x, 18, -16777216, false);
	}

	public void onFirstRender() {}
}
