package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.GenericType;

public class StringArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(String.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType) {
		return Arbitraries.string();
	}
}
