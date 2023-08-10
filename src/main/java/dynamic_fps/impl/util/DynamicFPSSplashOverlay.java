package dynamic_fps.impl.util;

public interface DynamicFPSSplashOverlay {
	public default boolean dynamic_fps$isReloadComplete() {
		throw new RuntimeException("Dynamic FPS' SplashOverlay mixin was not applied.");
	}
}
