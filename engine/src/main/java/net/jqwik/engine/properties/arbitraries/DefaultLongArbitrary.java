package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultLongArbitrary extends AbstractArbitraryBase implements LongArbitrary {

	private static final long DEFAULT_MIN = Long.MIN_VALUE;
	private static final long DEFAULT_MAX = Long.MAX_VALUE;

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultLongArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.valueOf(DEFAULT_MIN), BigInteger.valueOf(DEFAULT_MAX));
	}

	@Override
	public RandomGenerator<Long> generator(int genSize) {
		return generatingArbitrary.generator(genSize).map(BigInteger::longValueExact);
	}

	@Override
	public Optional<ExhaustiveGenerator<Long>> exhaustive(long maxNumberOfSamples) {
		return generatingArbitrary.exhaustive(maxNumberOfSamples).map(generator -> generator.map(BigInteger::longValueExact));
	}

	@Override
	public EdgeCases<Long> edgeCases() {
		return generatingArbitrary.edgeCases().map(BigInteger::longValueExact);
	}

	@Override
	public LongArbitrary withDistribution(final RandomDistribution distribution) {
		DefaultLongArbitrary clone = typedClone();
		clone.generatingArbitrary.distribution = distribution;
		return clone;
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

	@Override
	public LongArbitrary shrinkTowards(long target) {
		DefaultLongArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigInteger.valueOf(target);
		return clone;
	}

}
