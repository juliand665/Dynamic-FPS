package dynamic_fps.impl;

/**
 * An analog for device power states, applied to the Minecraft window.
 *
 * Power states are prioritized based on their order here, see DynamicFPSMod.checkForStateChanges for impl details.
 */
public enum PowerState {
	/**
	 * Window is currently focused.
	 */
	FOCUSED(false),

	/**
	 * Mouse positioned over unfocused window.
	 */
	HOVERED(true),

	/**
	 * Another application is focused.
	 */
	UNFOCUSED(true),

	/**
	 * Window minimized or otherwise hidden.
	 */
	INVISIBLE(true),

	/**
	 * User hasn't sent input for some time.
	 */
	ABANDONED(true);

	public final boolean configurable;

	private PowerState(boolean configurable) {
		this.configurable = configurable;
	}
}
