package net.jqwik.execution.providers;

import java.math.BigDecimal;

import net.jqwik.api.*;
import net.jqwik.execution.GenericType;

public class BigDecimalArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(BigDecimal.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType) {
		return Arbitraries.bigDecimal();
	}
}
