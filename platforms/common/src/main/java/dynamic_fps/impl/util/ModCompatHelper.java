package dynamic_fps.impl.util;

import dynamic_fps.impl.service.ModCompat;
import dynamic_fps.impl.service.Platform;

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
		Optional<Version> optional = Platform.getInstance().getModVersion("fastload");

		if (!optional.isPresent()) {
			return;
		}

		Version other;

		try {
			other = Version.of("3.4.0");
		} catch (Version.VersionParseException e) {
			throw new RuntimeException(e);
		}

		// If a version below 3.4.0 is present opt their custom world loading screen out of our optimization
		if (optional.get().compareTo(other) <= 0) {
			ModCompat.getInstance().getOptedOutScreens().add(
				"io.github.bumblesoftware.fastload.client.BuildingTerrainScreen"
			);
		}
	}
}
