package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

import java.util.function.*;

public class BooleanArbitraryProvider implements TypedArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType, boolean withName) {
		return !withName && targetType.isAssignableFrom(Boolean.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		return Arbitraries.of(true, false);
	}
}
