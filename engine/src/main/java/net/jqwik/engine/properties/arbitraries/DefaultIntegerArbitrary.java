package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultIntegerArbitrary extends AbstractArbitraryBase implements IntegerArbitrary {

	private static final int DEFAULT_MIN = Integer.MIN_VALUE;
	private static final int DEFAULT_MAX = Integer.MAX_VALUE;

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultIntegerArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.valueOf(DEFAULT_MIN), BigInteger.valueOf(DEFAULT_MAX));
	}

	@Override
	public RandomGenerator<Integer> generator(int genSize) {
		return generatingArbitrary.generator(genSize).map(BigInteger::intValueExact);
	}

	@Override
	public Optional<ExhaustiveGenerator<Integer>> exhaustive(long maxNumberOfSamples) {
		return generatingArbitrary.exhaustive(maxNumberOfSamples).map(generator -> generator.map(BigInteger::intValueExact));
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

	@Override
	public Arbitrary<Integer> shrinkTowards(int target) {
		DefaultIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigInteger.valueOf(target);
		return clone;
	}

}
