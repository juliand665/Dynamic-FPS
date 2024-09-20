package dynamic_fps.impl.service;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

class Services {
	static Platform PLATFORM = loadService(Platform.class);
	static ModCompat MOD_COMPAT = loadService(ModCompat.class);

	static <T> T loadService(Class<T> type) {
		try {
			return ServiceLoader.load(type).iterator().next();
		} catch (NoSuchElementException e) {
			throw new RuntimeException("Failed to load Dynamic FPS " + type.getSimpleName() + " service!");
		}
	}
}
