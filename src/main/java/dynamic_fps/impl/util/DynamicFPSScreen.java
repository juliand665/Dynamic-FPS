package dynamic_fps.impl.util;

public interface DynamicFPSScreen {
	public default boolean dynamic_fps$rendersBackground() {
		throw new RuntimeException("Dynamic FPS' Screen mixin was not applied.");
	}

	public default void dynamic_fps$setRendersBackground(boolean value) {
		throw new RuntimeException("Dynamic FPS' Screen mixin was not applied.");
	}
}
