package net.jqwik.execution.properties.providers;

import javaslang.test.Arbitrary;
import net.jqwik.api.properties.Generator;
import net.jqwik.execution.properties.GenericType;

import java.util.function.Function;

public class EnumArbitraryProvider implements TypedArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isEnum();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		//noinspection unchecked
		return Generator.of((Class<Enum>) targetType.getRawType());
	}
}
