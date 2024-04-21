package dynamic_fps.impl.util.duck;

public interface DuckScreen {
	public default boolean dynamic_fps$rendersBackground() {
		throw new RuntimeException("No implementation for dynamic_fps$rendersBackground was found.");
	}

	public default void dynamic_fps$setRendersBackground() {
		throw new RuntimeException("No implementation for dynamic_fps$rendersBackground was found.");
	}
}
