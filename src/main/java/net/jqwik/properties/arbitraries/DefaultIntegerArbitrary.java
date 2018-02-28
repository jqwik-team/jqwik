package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultIntegerArbitrary extends NullableArbitraryBase<Integer> implements IntegerArbitrary {

	private static final int DEFAULT_MIN = Integer.MIN_VALUE;
	private static final int DEFAULT_MAX = Integer.MAX_VALUE;

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultIntegerArbitrary() {
		super(Integer.class);
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.valueOf(DEFAULT_MIN), BigInteger.valueOf(DEFAULT_MAX));
	}

	@Override
	protected RandomGenerator<Integer> baseGenerator(int tries) {
		return generatingArbitrary.generator(tries).map(BigInteger::intValueExact);
	}

	@Override
	public IntegerArbitrary greaterOrEqual(int min) {
		DefaultIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.min = BigInteger.valueOf(min);
		return clone;
	}

	@Override
	public IntegerArbitrary lessOrEqual(int max) {
		DefaultIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.max = BigInteger.valueOf(max);
		return clone;
	}

}
