package net.jqwik.execution.providers;

import java.math.BigInteger;

import net.jqwik.api.*;
import net.jqwik.execution.GenericType;

public class BigIntegerArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(BigInteger.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType) {
		return Arbitraries.bigInteger();
	}
}
