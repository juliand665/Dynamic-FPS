package dynamic_fps.impl.util;

import java.util.Locale;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class EnumCodec<E extends Enum<E>> implements PrimitiveCodec<E> {
	private final E[] members;

	public EnumCodec(E[] members) {
		this.members = members;

		if (members.length == 0) {
			throw new RuntimeException("EnumCodec has no members!");
		}
	}

	@Override
	public <T> T write(DynamicOps<T> ops, E value) {
		return ops.createString(value.toString().toLowerCase(Locale.ROOT));
	}

	@Override
	public <T> DataResult<E> read(DynamicOps<T> ops, T input) {
		var value = ops.getStringValue(input).get().left();

		if (value.isEmpty()) {
			return DataResult.error(() -> this.getTypeName() + " must not be empty!");
		}

		var inner = value.get().toUpperCase(Locale.ROOT);

		for (var member : this.members) {
			if (member.name().equals(inner)) {
				return DataResult.success(member);
			}
		}

		return DataResult.error(() -> this.getTypeName() + " has no value " + inner + "!");
	}

	private String getTypeName() {
		return this.members[0].getDeclaringClass().getName();
	}
}
