package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultBigIntegerArbitrary extends NullableArbitraryBase<BigInteger> implements BigIntegerArbitrary {

	private static final BigInteger DEFAULT_MIN = BigInteger.valueOf(Long.MIN_VALUE);
	private static final BigInteger DEFAULT_MAX = BigInteger.valueOf(Long.MAX_VALUE);

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultBigIntegerArbitrary() {
		super(BigInteger.class);
		this.generatingArbitrary = new IntegralGeneratingArbitrary(DEFAULT_MIN, DEFAULT_MAX);
	}

	@Override
	public BigIntegerArbitrary greaterOrEqual(BigInteger min) {
		checkBoundaries(min);
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.min = min;
		return clone;
	}

	@Override
	public BigIntegerArbitrary lessOrEqual(BigInteger max) {
		checkBoundaries(max);
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.max = max;
		return clone;
	}

	@Override
	protected RandomGenerator<BigInteger> baseGenerator(int tries) {
		return generatingArbitrary.generator(tries);
	}

	private void checkBoundaries(BigInteger min) {
		if (min.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
			throw new JqwikException("Min  and max values must not be smaller than Long.MIN_VALUE");
		}
		if (min.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
			throw new JqwikException("Min  and max values must not be larger than Long.MAX_VALUE");
		}
	}
}
