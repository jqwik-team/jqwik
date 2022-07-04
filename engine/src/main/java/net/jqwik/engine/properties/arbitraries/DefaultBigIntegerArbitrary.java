package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultBigIntegerArbitrary extends TypedCloneable implements BigIntegerArbitrary {

	public static final BigInteger DEFAULT_MIN = BigInteger.valueOf(Long.MIN_VALUE);
	public static final BigInteger DEFAULT_MAX = BigInteger.valueOf(Long.MAX_VALUE);

	private IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultBigIntegerArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(DEFAULT_MIN, DEFAULT_MAX);
	}

	@Override
	public RandomGenerator<BigInteger> generator(int genSize) {
		return generatingArbitrary.generator(genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<BigInteger>> exhaustive(long maxNumberOfSamples) {
		return generatingArbitrary.exhaustive(maxNumberOfSamples);
	}

	@Override
	public EdgeCases<BigInteger> edgeCases(int maxEdgeCases) {
		return generatingArbitrary.edgeCases(maxEdgeCases);
	}

	@Override
	public Arbitrary<BigInteger> edgeCases(Consumer<EdgeCases.Config<BigInteger>> configurator) {
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary = (IntegralGeneratingArbitrary) generatingArbitrary.edgeCases(configurator);
		return clone;
	}

	@Override
	public BigIntegerArbitrary withDistribution(final RandomDistribution distribution) {
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.distribution = distribution;
		return clone;
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
	public BigIntegerArbitrary shrinkTowards(BigInteger target) {
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = target;
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultBigIntegerArbitrary that = (DefaultBigIntegerArbitrary) o;
		return generatingArbitrary.equals(that.generatingArbitrary);
	}

	@Override
	public int hashCode() {
		return generatingArbitrary.hashCode();
	}
}
