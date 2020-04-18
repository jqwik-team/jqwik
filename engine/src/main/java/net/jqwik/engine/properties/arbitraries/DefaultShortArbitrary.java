package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultShortArbitrary extends AbstractArbitraryBase implements ShortArbitrary {

	private static final short DEFAULT_MIN = Short.MIN_VALUE;
	private static final short DEFAULT_MAX = Short.MAX_VALUE;

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultShortArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.valueOf(DEFAULT_MIN), BigInteger.valueOf(DEFAULT_MAX));
	}

	@Override
	public RandomGenerator<Short> generator(int genSize) {
		return generatingArbitrary.generator(genSize).map(BigInteger::shortValueExact);
	}

	@Override
	public Optional<ExhaustiveGenerator<Short>> exhaustive(long maxNumberOfSamples) {
		return generatingArbitrary.exhaustive(maxNumberOfSamples).map(generator -> generator.map(BigInteger::shortValueExact));
	}

	@Override
	public EdgeCases<Short> edgeCases() {
		return generatingArbitrary.edgeCases().map(BigInteger::shortValueExact);
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

	@Override
	public Arbitrary<Short> shrinkTowards(short target) {
		DefaultShortArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigInteger.valueOf(target);
		return clone;
	}

}
