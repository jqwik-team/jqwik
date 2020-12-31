package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

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
	public EdgeCases<Integer> edgeCases() {
		return EdgeCasesSupport.map(generatingArbitrary.edgeCases(), BigInteger::intValueExact);
	}

	@Override
	public Arbitrary<Integer> edgeCases(Consumer<EdgeCases.Config<Integer>> configurator) {
		// TODO: Generalize for all number arbitraries
		Consumer<EdgeCases.Config<BigInteger>> integralConfigurator = bigIntegerConfig -> {
			EdgeCases.Config<Integer> integerConfig = new EdgeCases.Config<Integer>() {
				@Override
				public EdgeCases.Config<Integer> none() {
					bigIntegerConfig.none();
					return this;
				}

				@Override
				public EdgeCases.Config<Integer> filter(Predicate<Integer> filter) {
					bigIntegerConfig.filter(bigInteger -> filter.test(bigInteger.intValueExact()));
					return this;
				}

				@Override
				public EdgeCases.Config<Integer> add(Integer edgeCase) {
					bigIntegerConfig.add(BigInteger.valueOf(edgeCase));
					return this;
				}

				@Override
				public EdgeCases.Config<Integer> includeOnly(Integer... includedValues) {
					BigInteger[] includedBigIntegers = new BigInteger[includedValues.length];
					for (int i = 0; i < includedValues.length; i++) {
						includedBigIntegers[i] = BigInteger.valueOf(includedValues[i]);
					}
					bigIntegerConfig.includeOnly(includedBigIntegers);
					return this;
				}
			};
			configurator.accept(integerConfig);
		};
		return generatingArbitrary.edgeCases(integralConfigurator).map(BigInteger::intValueExact);
	}

	@Override
	public IntegerArbitrary withDistribution(final RandomDistribution distribution) {
		DefaultIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.distribution = distribution;
		return clone;
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
	public IntegerArbitrary shrinkTowards(int target) {
		DefaultIntegerArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigInteger.valueOf(target);
		return clone;
	}

}
