package dynamicfps;

import dynamicfps.util.KeyBindingHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.locks.LockSupport;

public class DynamicFPSMod implements ModInitializer {
	public static final String MOD_ID = "dynamicfps";
	
	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}
	
	private static long lastRender;
	
	private static boolean isForcingLowFPS = false;
	
	public static boolean isForcingLowFPS() {
		return isForcingLowFPS;
	}
	
	private static FabricKeyBinding toggleKeyBinding = FabricKeyBinding.Builder.create(
		identifier("toggle"),
		InputUtil.Type.KEYSYM,
		InputUtil.UNKNOWN_KEYCODE.getKeyCode(),
		"key.categories.misc"
	).build();
	
	@Override
	public void onInitialize() {
		KeyBindingRegistry.INSTANCE.register(toggleKeyBinding);
		
		ClientTickCallback.EVENT.register(new KeyBindingHandler(
			toggleKeyBinding,
			() -> isForcingLowFPS = !isForcingLowFPS
		));
		
		HudRenderCallback.EVENT.register(new HudInfoRenderer());
	}
	
	/**
	 Determines whether the game should render anything at this time. If not, blocks for a short time.
	 
	 @return whether or not the game should be rendered after this.
	 */
	public static boolean checkForRender() {
		MinecraftClient client = MinecraftClient.getInstance();
		Window window = ((WindowHolder) client).getWindow();
		
		long currentTime = Util.getMeasuringTimeMs();
		long timeSinceLastRender = currentTime - lastRender;
		
		boolean isVisible = GLFW.glfwGetWindowAttrib(window.getHandle(), GLFW.GLFW_VISIBLE) != 0;
		boolean isFocusPaused = client.options.pauseOnLostFocus && !client.isWindowFocused();
		boolean shouldReduceFPS = isForcingLowFPS || isFocusPaused;
		
		boolean shouldRender = isVisible && (!shouldReduceFPS || timeSinceLastRender > 1000);
		if (shouldRender) {
			lastRender = currentTime;
		} else {
			LockSupport.parkNanos("waiting to render", 30_000_000); // 30 ms
		}
		return shouldRender;
	}
	
	public interface WindowHolder {
		Window getWindow();
	}
}
