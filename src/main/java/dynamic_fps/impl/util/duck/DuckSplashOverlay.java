package dynamic_fps.impl.util.duck;

public interface DuckSplashOverlay {
	public default boolean dynamic_fps$isReloadComplete() {
		throw new RuntimeException("No implementation for dynamic_fps$isReloadComplete was found.");
	}
}
