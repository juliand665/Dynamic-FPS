package dynamic_fps.impl.feature.battery;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dynamic_fps.impl.util.ResourceLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseToast implements Toast {
	private long firstRender;

	protected Component title;
	protected Component description;
	protected @Nullable ResourceLocation icon;

	private static final Minecraft MINECRAFT = Minecraft.getInstance();

	private static final ResourceLocation MOD_ICON = ResourceLocations.of("dynamic_fps", "textures/battery/toast/background_icon.png");
	private static final ResourceLocation BACKGROUND_IMAGE = ResourceLocations.of("dynamic_fps", "textures/battery/toast/background.png");

	protected BaseToast(Component title, Component description, @Nullable ResourceLocation icon) {
		this.title = title;
		this.description = description;

		this.icon = icon;
	}

	@Override
	public @NotNull Visibility render(PoseStack poseStack, ToastComponent toastComponent, long currentTime) {
		if (this.firstRender == 0) {
			this.onFirstRender();
			this.firstRender = currentTime;
		}

		RenderSystem.setShaderTexture(0, BACKGROUND_IMAGE);
		// resource, x, y, z, ?, ?, width, height, width, height
		GuiComponent.blit(poseStack, 0, 0, 0.0f, 0.0f, this.width(), this.height(), this.width(), this.height());

		int x = 8;

		if (this.icon != null) {
			x += 22;

			RenderSystem.setShaderTexture(0, MOD_ICON);
			GuiComponent.blit(poseStack, 2, 2, 0.0f, 0.0f, 8, 8, 8, 8);
			RenderSystem.setShaderTexture(0, this.icon);
			GuiComponent.blit(poseStack, 8, 8, 0.0f, 0.0f, 16, 16, 16, 16);
		}

		MINECRAFT.font.draw(poseStack, this.title, x, 7, 0x5f3315);
		MINECRAFT.font.draw(poseStack, this.description, x, 18, -16777216);

		return currentTime - this.firstRender >= 5000.0 ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
	}

	public void onFirstRender() {}
}
