package dynamic_fps.impl.util.duck;

public interface DuckScreen {
	default boolean dynamic_fps$rendersBackground() {
		throw new RuntimeException("No implementation for dynamic_fps$rendersBackground was found.");
	}

	default void dynamic_fps$setRendersBackground() {
		throw new RuntimeException("No implementation for dynamic_fps$rendersBackground was found.");
	}
}
