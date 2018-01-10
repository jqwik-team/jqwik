package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.*;

import java.util.*;
import java.util.function.*;

public class OptionalArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(Optional.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeSupplier) {
		GenericType innerType = targetType.getTypeArguments()[0];
		return subtypeSupplier.apply(innerType) //
			.map(Arbitraries::optionalOf) //
			.orElse(null);
	}
}
