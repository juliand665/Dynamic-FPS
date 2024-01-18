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
	FOCUSED(false, true),

	/**
	 * Mouse positioned over unfocused window.
	 */
	HOVERED(true, false),

	/**
	 * Another application is focused.
	 */
	UNFOCUSED(true, false),

	/**
	 * User hasn't sent input for some time.
	 */
	ABANDONED(true, true),

	/**
	 * Window minimized or otherwise hidden.
	 */
	INVISIBLE(true, false);

	public final boolean configurable;
	public final boolean windowActive;

	private PowerState(boolean configurable, boolean windowActive) {
		this.configurable = configurable;
		this.windowActive = windowActive;
	}
}
