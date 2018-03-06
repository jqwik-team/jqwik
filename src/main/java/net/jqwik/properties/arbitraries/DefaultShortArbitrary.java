package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultShortArbitrary extends AbstractArbitraryBase implements ShortArbitrary {

	private static final short DEFAULT_MIN = Short.MIN_VALUE;
	private static final short DEFAULT_MAX = Short.MAX_VALUE;

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultShortArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.valueOf(DEFAULT_MIN), BigInteger.valueOf(DEFAULT_MAX));
	}

	@Override
	public RandomGenerator<Short> generator(int tries) {
		return generatingArbitrary.generator(tries).map(BigInteger::shortValueExact);
	}

	@Override
	public ShortArbitrary greaterOrEqual(short min) {
		DefaultShortArbitrary clone = typedClone();
		clone.generatingArbitrary.min = BigInteger.valueOf(min);
		return clone;
	}

	@Override
	public ShortArbitrary lessOrEqual(short max) {
		DefaultShortArbitrary clone = typedClone();
		clone.generatingArbitrary.max = BigInteger.valueOf(max);
		return clone;
	}

}
