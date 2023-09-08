package dynamic_fps.impl;

import java.util.Locale;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

/**
 * An analog for device power states, applied to the Minecraft window.
 *
 * Power states are prioritized based on their order here, see DynamicFPSMod.checkForStateChanges for impl details.
 */
public enum PowerState {
	/*
	 * Window is currently focused.
	 */
	FOCUSED(false),

	/*
	 * Mouse positioned over unfocused window.
	 */
	HOVERED(true),

	/*
	 * Another application is focused.
	 */
	UNFOCUSED(true),

	/*
	 * Window minimized or otherwise hidden.
	 */
	INVISIBLE(true),

	/*
	 * User is currently on the pause screen.
	 */
	SUSPENDED(false);

	public final boolean configurable;

	public static final Codec<PowerState> CODEC = new PrimitiveCodec<PowerState>() {
		@Override
		public <T> T write(DynamicOps<T> ops, PowerState value) {
			return ops.createString(value.toString().toLowerCase(Locale.ROOT));
		}

		@Override
		public <T> DataResult<PowerState> read(DynamicOps<T> ops, T input) {
			var value = ops.getStringValue(input).get().left();

			if (value.isEmpty()) {
				return DataResult.error(() -> "Power state must not be empty!");
			} else {
				return DataResult.success(PowerState.valueOf(value.get().toUpperCase(Locale.ROOT)));
			}
		}
	};

	private PowerState(boolean configurable) {
		this.configurable = configurable;
	}
}
