package net.jqwik.providers;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class OptionalArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isOfType(Optional.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeSupplier) {
		GenericType innerType = targetType.getTypeArguments().get(0);
		return subtypeSupplier.apply(innerType) //
				.map(Arbitrary::optional) //
				.orElse(null);
	}
}
