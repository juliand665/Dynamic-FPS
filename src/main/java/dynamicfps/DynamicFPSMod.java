package dynamicfps;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.SystemUtil;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.locks.LockSupport;

public class DynamicFPSMod implements ModInitializer {
	private static long lastRender;
	
	@Override
	public void onInitialize() {}
	
	/**
	 Determines whether the game should render anything at this time. If not, blocks for a short time.
	 
	 @return whether or not the game should be rendered after this.
	 */
	public static boolean checkForRender() {
		MinecraftClient client = MinecraftClient.getInstance();
		Window window = ((WindowHolder) client).getWindow();
		
		long currentTime = SystemUtil.getMeasuringTimeMs();
		
		boolean isVisible = GLFW.glfwGetWindowAttrib(window.getHandle(), GLFW.GLFW_VISIBLE) != 0;
		boolean isFocusPaused = client.options.pauseOnLostFocus && !client.isWindowFocused();
		long timeSinceLastRender = currentTime - lastRender;
		
		boolean shouldRender = isVisible && !(isFocusPaused && timeSinceLastRender < 1000);
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
