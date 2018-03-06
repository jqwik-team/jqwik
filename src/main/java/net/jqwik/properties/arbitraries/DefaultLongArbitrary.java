package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultLongArbitrary extends AbstractArbitraryBase implements LongArbitrary {

	private static final long DEFAULT_MIN = Long.MIN_VALUE;
	private static final long DEFAULT_MAX = Long.MAX_VALUE;

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultLongArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.valueOf(DEFAULT_MIN), BigInteger.valueOf(DEFAULT_MAX));
	}

	@Override
	public RandomGenerator<Long> generator(int tries) {
		return generatingArbitrary.generator(tries).map(BigInteger::longValueExact);
	}

	@Override
	public LongArbitrary greaterOrEqual(long min) {
		DefaultLongArbitrary clone = typedClone();
		clone.generatingArbitrary.min = BigInteger.valueOf(min);
		return clone;
	}

	@Override
	public LongArbitrary lessOrEqual(long max) {
		DefaultLongArbitrary clone = typedClone();
		clone.generatingArbitrary.max = BigInteger.valueOf(max);
		return clone;
	}

}
