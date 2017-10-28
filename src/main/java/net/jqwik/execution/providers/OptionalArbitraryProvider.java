package net.jqwik.execution.providers;

import java.util.Optional;
import java.util.function.Function;

import net.jqwik.api.*;
import net.jqwik.execution.GenericType;

public class OptionalArbitraryProvider implements GenericArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(Optional.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		GenericType innerType = targetType.getTypeArguments()[0];
		Arbitrary<?> innerArbitrary = subtypeSupplier.apply(innerType);
		if (innerArbitrary != null)
			return Arbitraries.optionalOf(innerArbitrary);
		return null;
	}
}
