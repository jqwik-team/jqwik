package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultShortArbitrary extends TypedCloneable implements ShortArbitrary {

	private static final short DEFAULT_MIN = Short.MIN_VALUE;
	private static final short DEFAULT_MAX = Short.MAX_VALUE;

	private IntegralGeneratingArbitrary generatingArbitrary;

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
	public EdgeCases<Short> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.map(generatingArbitrary.edgeCases(maxEdgeCases), BigInteger::shortValueExact);
	}

	@Override
	public Arbitrary<Short> edgeCases(Consumer<EdgeCases.Config<Short>> configurator) {
		Consumer<EdgeCases.Config<BigInteger>> integralConfigurator = new MappedEdgeCasesConsumer<>(
				configurator,
				BigInteger::shortValueExact,
				(Function<Short, BigInteger>) BigInteger::valueOf
		);
		DefaultShortArbitrary clone = typedClone();
		clone.generatingArbitrary = (IntegralGeneratingArbitrary) generatingArbitrary.edgeCases(integralConfigurator);
		return clone;
	}

	@Override
	public ShortArbitrary withDistribution(final RandomDistribution distribution) {
		DefaultShortArbitrary clone = typedClone();
		clone.generatingArbitrary.distribution = distribution;
		return clone;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultShortArbitrary that = (DefaultShortArbitrary) o;
		return generatingArbitrary.equals(that.generatingArbitrary);
	}

	@Override
	public int hashCode() {
		return generatingArbitrary.hashCode();
	}
}
