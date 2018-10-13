package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

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
	//TODO: Generalize for all Integrals and move to IntegralGeneratingArbitrary
	public Optional<ExhaustiveGenerator<Integer>> exhaustive() {
		BigInteger maxCount = generatingArbitrary.max.subtract(generatingArbitrary.min).add(BigInteger.ONE);

		if (maxCount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
			return Optional.empty();
		} else {
			int begin = generatingArbitrary.min.intValueExact();
			int end = generatingArbitrary.max.intValueExact() + 1;
			return ExhaustiveGenerators.fromIterator(IntStream.range(begin, end).iterator(), maxCount.longValueExact());
		}
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
