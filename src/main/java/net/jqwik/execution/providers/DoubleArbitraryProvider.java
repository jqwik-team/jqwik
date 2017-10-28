package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.GenericType;

public class DoubleArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(Double.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType) {
		return Arbitraries.doubles();
	}
}
