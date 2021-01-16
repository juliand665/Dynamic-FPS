package dynamicfps;

import dynamicfps.util.KeyBindingHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.locks.LockSupport;

public class DynamicFPSMod implements ModInitializer {
	public static final String MOD_ID = "dynamicfps";
	
	private static long lastRender;

	static DynamicFPSConfig config = null;
	
	private static boolean isForcingLowFPS = false;
	
	// we always render one last frame before actually reducing FPS, so the hud text shows up instantly when forcing low fps.
	// additionally, this would enable mods which render differently while mc is inactive.
	private static boolean hasRenderedLastFrame = false;
	
	public static boolean isForcingLowFPS() {
		return isForcingLowFPS;
	}
	
	private static final KeyBinding toggleKeyBinding = new KeyBinding(
		"key." + MOD_ID + ".toggle",
		InputUtil.Type.KEYSYM,
		InputUtil.UNKNOWN_KEY.getCode(),
		"key.categories.misc"
	);
	
	@Override
	public void onInitialize() {
		config = DynamicFPSConfig.getConfig();

		KeyBindingHelper.registerKeyBinding(toggleKeyBinding);
		
		ClientTickEvents.END_CLIENT_TICK.register(new KeyBindingHandler(
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
		boolean shouldReduceFPS = isForcingLowFPS || (!client.isWindowFocused()) && config.enableUnfocusedFps;
		if (!shouldReduceFPS && hasRenderedLastFrame) {
			hasRenderedLastFrame = false;
		}
		
		boolean shouldRender = isVisible && (!shouldReduceFPS || timeSinceLastRender > config.millisecondsTarget);
		if (shouldRender) {
			lastRender = currentTime;
		} else {
			if (!hasRenderedLastFrame) {
				hasRenderedLastFrame = true;
				return true; // render one last frame before reducing, to make sure differences in this state show up instantly.
			}
			LockSupport.parkNanos("waiting to render", 15_000_000); // 15 ms; reduced from the original 30 ms to allow ~60 FPS limit
		}
		return shouldRender;
	}
	
	public interface WindowHolder {
		Window getWindow();
	}
}
