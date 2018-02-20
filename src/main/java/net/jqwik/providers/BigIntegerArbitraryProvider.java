package net.jqwik.providers;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class BigIntegerArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isOfType(BigInteger.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.bigIntegers();
	}

	public BigIntegerArbitrary configure(BigIntegerArbitrary arbitrary, LongRange range) {
		return arbitrary.withMin(BigInteger.valueOf(range.min())).withMax(BigInteger.valueOf(range.max()));
	}

}
