package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultBigIntegerArbitrary extends AbstractArbitraryBase implements BigIntegerArbitrary {

	private static final BigInteger DEFAULT_MIN = BigInteger.valueOf(Long.MIN_VALUE);
	private static final BigInteger DEFAULT_MAX = BigInteger.valueOf(Long.MAX_VALUE);

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultBigIntegerArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(DEFAULT_MIN, DEFAULT_MAX);
	}

	@Override
	public BigIntegerArbitrary greaterOrEqual(BigInteger min) {
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.min = (min == null ? DEFAULT_MIN : min);
		return clone;
	}

	@Override
	public BigIntegerArbitrary lessOrEqual(BigInteger max) {
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.max = (max == null ? DEFAULT_MAX : max);
		return clone;
	}

	@Override
	public RandomGenerator<BigInteger> generator(int tries) {
		return generatingArbitrary.generator(tries);
	}
}
