package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.GenericType;

public class EnumArbitraryProvider implements SimpleArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isEnum();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType) {
		// noinspection unchecked
		return Arbitraries.of((Class<Enum>) targetType.getRawType());
	}
}
