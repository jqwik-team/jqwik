package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.function.*;

public class OptionalArbitraryProvider implements TypedArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType, boolean withName) {
		return targetType.isAssignableFrom(Optional.class) && targetType.isGeneric();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		// TODO: Should also generate null values within Optional
		GenericType innerType = targetType.getTypeArguments()[0];
		Arbitrary<?> innerArbitrary = subtypeSupplier.apply(innerType);
		if (innerArbitrary != null)
			return Arbitraries.optionalOf(innerArbitrary);
		return null;
	}
}
