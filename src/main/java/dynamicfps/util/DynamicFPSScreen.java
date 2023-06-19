package dynamicfps.util;

public interface DynamicFPSScreen {
	public default boolean dynamicfps$rendersBackground() {
		throw new RuntimeException("Dynamic FPS' Screen mixin was not applied.");
	}
}
