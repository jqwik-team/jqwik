package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.GenericType;

public class IntegerArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(Integer.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType) {
		return Arbitraries.integer();
	}
}
