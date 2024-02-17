package dynamic_fps.impl.util;

import dynamic_fps.impl.service.ModCompat;
import dynamic_fps.impl.service.Platform;

import java.util.Arrays;
import java.util.Optional;

public class ModCompatHelper {
	public static void init() {
		fixFastloadSoftLock();
	}

	/**
	 * Fix softlock in combination with Fastload <=3.4.0 due to our screen / loading overlay optimization.
	 *
	 * See the <a href="https://github.com/juliand665/Dynamic-FPS/issues/129">issue report</a> for more info.
	 */
	private static void fixFastloadSoftLock() {
		Optional<String> optional = Platform.getInstance().getModVersion("fastload");

		if (!optional.isPresent()) {
			return;
		}

		String[] parts = optional.get().split("\\.");
		int[] version = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();

		if (version.length < 3) {
			Logging.getLogger().warn("Unable to parse Fastload version: {}!", optional.get());
			return;
		}

		// If a version below 3.4.0 is present opt their custom world loading screen out of our optimization
		if (!(version[0] > 3 || version[0] == 3 && (version[1] > 4 || version[1] == 4 && version[2] > 0))) {
			ModCompat.getInstance().getOptedOutScreens().add(
				"io.github.bumblesoftware.fastload.client.BuildingTerrainScreen"
			);

		}
	}
}
