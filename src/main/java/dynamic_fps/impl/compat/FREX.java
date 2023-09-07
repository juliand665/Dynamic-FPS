package dynamic_fps.impl.compat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import dynamic_fps.impl.DynamicFPSMod;

/**
 * <p>
 * Implements the FREX Flawless Frames API to allow other mods to request all
 * frames to be processed until requested to
 * go back to normal operation, such as ReplayMod rendering a video.
 * <p>
 * See https://github.com/grondag/frex/pull/9
 */
public final class FREX implements ClientModInitializer {
	private static final Set<Object> ACTIVE = ConcurrentHashMap.newKeySet();

	private static final class Listener implements Consumer<Boolean> {
		private final String name;

		private Listener(String name) {
			this.name = name;
		}

		@Override
		public void accept(Boolean enabled) {
			if (enabled) {
				ACTIVE.add(this.name);
			} else {
				ACTIVE.remove(this.name);
			}

			DynamicFPSMod.onStatusChanged();
		}
	}

	public interface ListenerConsumer extends Consumer<Function<String, Consumer<Boolean>>> {}

	/**
	 * Returns whether one or more mods have requested Flawless Frames to be active,
	 * and therefore frames should not be skipped.
	 */
	public static boolean isFlawlessFramesActive() {
		return !ACTIVE.isEmpty();
	}

	@Override
	public void onInitializeClient() {
		FabricLoader.getInstance().getEntrypoints("frex_flawless_frames", ListenerConsumer.class).forEach(api -> api.accept(Listener::new));
	}
}
