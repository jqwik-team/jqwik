package net.jqwik.execution.providers;

import java.math.*;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

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
