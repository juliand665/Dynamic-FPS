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

import javax.annotation.Nullable;
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
		config = DynamicFPSConfig.load();
		
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
		long currentTime = Util.getMeasuringTimeMs();
		long timeSinceLastRender = currentTime - lastRender;
		
		if (!checkForRender(timeSinceLastRender)) return false;
		
		lastRender = currentTime;
		return true;
	}
	
	private static boolean checkForRender(long timeSinceLastRender) {
		Integer fpsOverride = fpsOverride();
		if (fpsOverride == null) {
			hasRenderedLastFrame = false;
			return true;
		}
		
		if (!hasRenderedLastFrame) {
			// render one last frame before reducing, to make sure differences in this state show up instantly.
			hasRenderedLastFrame = true;
			return true;
		}
		
		if (fpsOverride == 0) {
			idle(1000);
			return false;
		}
		
		long frameTime = 1000 / fpsOverride;
		boolean shouldSkipRender = timeSinceLastRender < frameTime;
		if (!shouldSkipRender) return true;
		
		idle(frameTime);
		return false;
	}
	
	/**
	 force minecraft to idle because otherwise we'll be busy checking for render again and again
	 */
	private static void idle(long waitMillis) {
		// cap at 30 ms before we check again so user doesn't have to wait long after tabbing back in
		waitMillis = Math.min(waitMillis, 30);
		LockSupport.parkNanos("waiting to render", waitMillis * 1_000_000);
	}
	
	@Nullable
	private static Integer fpsOverride() {
		MinecraftClient client = MinecraftClient.getInstance();
		Window window = ((WindowHolder) client).getWindow();
		
		boolean isVisible = GLFW.glfwGetWindowAttrib(window.getHandle(), GLFW.GLFW_VISIBLE) != 0;
		if (!isVisible) return 0;
		
		if (isForcingLowFPS) return config.unfocusedFPS;
		
		if (config.restoreFPSWhenHovered) {
			boolean isHovered = GLFW.glfwGetWindowAttrib(window.getHandle(), GLFW.GLFW_HOVERED) != 0;
			if (isHovered) return null;
		}
		
		if (config.reduceFPSWhenUnfocused && !client.isWindowFocused()) return config.unfocusedFPS;
		
		return null;
	}
	
	public interface WindowHolder {
		Window getWindow();
	}
}
