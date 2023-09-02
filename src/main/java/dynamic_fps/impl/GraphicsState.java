package dynamic_fps.impl;

import java.util.Locale;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

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

	public static final Codec<GraphicsState> CODEC = new PrimitiveCodec<GraphicsState>() {
		@Override
		public <T> T write(DynamicOps<T> ops, GraphicsState value) {
			return ops.createString(value.toString().toLowerCase(Locale.ROOT));
		}

		@Override
		public <T> DataResult<GraphicsState> read(DynamicOps<T> ops, T input) {
			var value = ops.getStringValue(input).get().left();

			if (value.isEmpty()) {
				return DataResult.error(() -> "Graphics state must not be empty!");
			} else {
				return DataResult.success(GraphicsState.valueOf(value.get().toUpperCase(Locale.ROOT)));
			}
		}
	};
}
