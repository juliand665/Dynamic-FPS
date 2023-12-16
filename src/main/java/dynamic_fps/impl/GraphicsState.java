package dynamic_fps.impl;

/*
 * Graphics settings to apply within a given power state.
 */
public enum GraphicsState {
	/*
	 * User-defined graphics settings via the options menu.
	 */
	DEFAULT,

	/*
	 * Reduce graphics settings which do not cause the world to reload.
	 */
	REDUCED,

	/*
	 * Reduce graphics settings to minimal values, this will reload the world!
	 */
	MINIMAL;
}
