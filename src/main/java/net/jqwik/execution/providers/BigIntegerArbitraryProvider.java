package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.*;

import java.math.*;
import java.util.*;
import java.util.function.*;

public class BigIntegerArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(BigInteger.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.bigIntegers();
	}
}
