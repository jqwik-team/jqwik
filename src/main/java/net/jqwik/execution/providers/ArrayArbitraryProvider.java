package net.jqwik.execution.providers;

import java.util.function.Function;

import net.jqwik.api.*;
import net.jqwik.execution.GenericType;

public class ArrayArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isArray();
	}

	@Override
	public boolean needsSubtypeProvider() {
		return true;
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeProvider) {
		return Arbitraries.arrayOf(targetType.getRawType(), subtypeProvider.apply(targetType.getComponentType()));
	}
}
