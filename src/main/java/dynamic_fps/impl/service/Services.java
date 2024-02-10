package dynamic_fps.impl.service;

import java.util.Optional;
import java.util.ServiceLoader;

class Services {
	static Platform PLATFORM = loadService(Platform.class);

	static <T> T loadService(Class<T> type) {
		Optional<T> optional = ServiceLoader.load(type).findFirst();

		if (optional.isPresent()) {
			return optional.get();
		} else {
			throw new RuntimeException("Failed to load Dynamic FPS " + type.getSimpleName() + " service!");
		}
	}
}
