package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

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
