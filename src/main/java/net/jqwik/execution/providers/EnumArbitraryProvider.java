package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

import java.util.function.*;

public class EnumArbitraryProvider implements TypedArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType, boolean withName) {
		return !withName && targetType.isEnum();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		// noinspection unchecked
		return Arbitraries.of((Class<Enum>) targetType.getRawType());
	}
}
