package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultLongArbitrary extends TypedCloneable implements LongArbitrary {

	private static final long DEFAULT_MIN = Long.MIN_VALUE;
	private static final long DEFAULT_MAX = Long.MAX_VALUE;

	private IntegralGeneratingArbitrary generatingArbitrary;

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
	public EdgeCases<Long> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.map(generatingArbitrary.edgeCases(maxEdgeCases), BigInteger::longValueExact);
	}

	@Override
	public Arbitrary<Long> edgeCases(Consumer<EdgeCases.Config<Long>> configurator) {
		Consumer<EdgeCases.Config<BigInteger>> integralConfigurator = new MappedEdgeCasesConsumer<>(
				configurator,
				BigInteger::longValueExact,
				BigInteger::valueOf
		);
		DefaultLongArbitrary clone = typedClone();
		clone.generatingArbitrary = (IntegralGeneratingArbitrary) generatingArbitrary.edgeCases(integralConfigurator);
		return clone;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultLongArbitrary that = (DefaultLongArbitrary) o;
		return generatingArbitrary.equals(that.generatingArbitrary);
	}

	@Override
	public int hashCode() {
		return generatingArbitrary.hashCode();
	}
}
