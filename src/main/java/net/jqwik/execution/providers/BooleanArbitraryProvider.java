package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.newArbitraries.*;

public class BooleanArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(Boolean.class);
	}

	@Override
	public NArbitrary<?> provideFor(GenericType targetType) {
		return NArbitraries.of(true, false);
	}
}
