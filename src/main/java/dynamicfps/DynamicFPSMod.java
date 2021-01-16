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
		MinecraftClient client = MinecraftClient.getInstance();
		Window window = ((WindowHolder) client).getWindow();
		
		long currentTime = Util.getMeasuringTimeMs();
		long timeSinceLastRender = currentTime - lastRender;
		
		boolean isVisible = GLFW.glfwGetWindowAttrib(window.getHandle(), GLFW.GLFW_VISIBLE) != 0;
		boolean shouldReduceFPS = isForcingLowFPS
			|| config.reduceFPSWhenUnfocused && !client.isWindowFocused();
		if (!shouldReduceFPS && hasRenderedLastFrame) {
			hasRenderedLastFrame = false;
		}
		
		boolean shouldNeverRender = !isVisible || config.unfocusedFPS == 0;
		long unfocusedFrameTimeMillis = shouldNeverRender ? 1000 : 1000 / config.unfocusedFPS;
		boolean shouldSkipRender = shouldNeverRender
			|| shouldReduceFPS && timeSinceLastRender < unfocusedFrameTimeMillis;
		if (shouldSkipRender) {
			if (!hasRenderedLastFrame) {
				hasRenderedLastFrame = true;
				return true; // render one last frame before reducing, to make sure differences in this state show up instantly.
			}
			
			// force minecraft to idle because otherwise we'll be busy checking for render again and again
			long waitMillis = Math.min(unfocusedFrameTimeMillis, 30); // at most 30 ms before we check again so user doesn't have to wait long after tabbing back in
			LockSupport.parkNanos("waiting to render", waitMillis * 1_000_000);
		} else {
			lastRender = currentTime;
		}
		return !shouldSkipRender;
	}
	
	public interface WindowHolder {
		Window getWindow();
	}
}
