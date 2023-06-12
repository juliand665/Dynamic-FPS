package dynamicfps.util;

public interface DynamicFPSSplashOverlay {
	public default boolean dynamicfps$isReloadComplete() {
		throw new RuntimeException("Dynamic FPS' SplashOverlay mixin was not applied.");
	}
}
