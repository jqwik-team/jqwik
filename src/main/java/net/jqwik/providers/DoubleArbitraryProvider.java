package net.jqwik.providers;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class DoubleArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isCompatibleWith(Double.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.doubles();
	}

	public DoubleArbitrary configure(DoubleArbitrary arbitrary, DoubleRange range) {
		return arbitrary.withMin(range.min()).withMax(range.max());
	}

}
