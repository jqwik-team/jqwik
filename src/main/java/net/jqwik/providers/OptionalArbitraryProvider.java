package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;

public class OptionalArbitraryProvider extends NullableArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isOfType(Optional.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeSupplier) {
		GenericType innerType = targetType.getTypeArguments()[0];
		return subtypeSupplier.apply(innerType) //
			.map(Arbitraries::optionalOf) //
			.orElse(null);
	}
}
