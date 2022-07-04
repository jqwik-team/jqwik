package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultByteArbitrary extends TypedCloneable implements ByteArbitrary {

	private static final byte DEFAULT_MIN = Byte.MIN_VALUE;
	private static final byte DEFAULT_MAX = Byte.MAX_VALUE;

	private IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultByteArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.valueOf(DEFAULT_MIN), BigInteger.valueOf(DEFAULT_MAX));
	}

	@Override
	public RandomGenerator<Byte> generator(int genSize) {
		return generatingArbitrary.generator(genSize).map(BigInteger::byteValueExact);
	}

	@Override
	public Optional<ExhaustiveGenerator<Byte>> exhaustive(long maxNumberOfSamples) {
		return generatingArbitrary.exhaustive(maxNumberOfSamples).map(generator -> generator.map(BigInteger::byteValueExact));
	}

	@Override
	public EdgeCases<Byte> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.map(generatingArbitrary.edgeCases(maxEdgeCases), BigInteger::byteValueExact);
	}

	@Override
	public Arbitrary<Byte> edgeCases(Consumer<EdgeCases.Config<Byte>> configurator) {
		Consumer<EdgeCases.Config<BigInteger>> integralConfigurator = new MappedEdgeCasesConsumer<>(
				configurator,
				BigInteger::byteValueExact,
				(Function<Byte, BigInteger>) BigInteger::valueOf
		);
		DefaultByteArbitrary clone = typedClone();
		clone.generatingArbitrary = (IntegralGeneratingArbitrary) generatingArbitrary.edgeCases(integralConfigurator);
		return clone;
	}

	@Override
	public ByteArbitrary withDistribution(final RandomDistribution distribution) {
		DefaultByteArbitrary clone = typedClone();
		clone.generatingArbitrary.distribution = distribution;
		return clone;
	}

	@Override
	public ByteArbitrary greaterOrEqual(byte min) {
		DefaultByteArbitrary clone = typedClone();
		clone.generatingArbitrary.min = BigInteger.valueOf(min);
		return clone;
	}

	@Override
	public ByteArbitrary lessOrEqual(byte max) {
		DefaultByteArbitrary clone = typedClone();
		clone.generatingArbitrary.max = BigInteger.valueOf(max);
		return clone;
	}

	@Override
	public ByteArbitrary shrinkTowards(int target) {
		DefaultByteArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigInteger.valueOf(target);
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultByteArbitrary that = (DefaultByteArbitrary) o;
		return generatingArbitrary.equals(that.generatingArbitrary);
	}

	@Override
	public int hashCode() {
		return generatingArbitrary.hashCode();
	}
}
