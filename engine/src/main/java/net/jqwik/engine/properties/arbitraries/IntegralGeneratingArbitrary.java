package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

import static java.math.BigInteger.*;

class IntegralGeneratingArbitrary extends TypedCloneable implements Arbitrary<BigInteger> {

	BigInteger min;
	BigInteger max;
	BigInteger shrinkingTarget;
	RandomDistribution distribution = RandomDistribution.biased();

	private Consumer<EdgeCases.Config<BigInteger>> edgeCasesConfigurator = EdgeCases.Config.noConfig();

	IntegralGeneratingArbitrary(BigInteger defaultMin, BigInteger defaultMax) {
		this.min = defaultMin;
		this.max = defaultMax;
		this.shrinkingTarget = null;
	}

	@Override
	public RandomGenerator<BigInteger> generator(int genSize) {
		return RandomGenerators.bigIntegers(min, max, shrinkingTarget(), distribution);
	}

	@Override
	public Optional<ExhaustiveGenerator<BigInteger>> exhaustive(long maxNumberOfSamples) {
		BigInteger maxCount = max.subtract(min).add(BigInteger.ONE);

		// Necessary because maxCount could be larger than Long.MAX_VALUE
		if (maxCount.compareTo(valueOf(maxNumberOfSamples)) > 0) {
			return Optional.empty();
		} else {
			return ExhaustiveGenerators.fromIterable(RangeIterator::new, maxCount.longValueExact(), maxNumberOfSamples);
		}
	}

	@Override
	public EdgeCases<BigInteger> edgeCases(int maxEdgeCases) {
		Range<BigInteger> range = Range.of(min, max);
		BigInteger shrinkingTarget = shrinkingTarget();
		Function<Integer, EdgeCases<BigInteger>> edgeCasesCreator = m -> {
			List<Shrinkable<BigInteger>> shrinkables =
				streamDefaultEdgeCases()
					.map(value -> new ShrinkableBigInteger(
						value,
						range,
						shrinkingTarget
					))
					.limit(Math.max(0, m))
					.collect(Collectors.toList());
			return EdgeCasesSupport.fromShrinkables(shrinkables);
		};
		IntegralEdgeCasesConfiguration configuration = new IntegralEdgeCasesConfiguration(range, shrinkingTarget);
		return configuration.configure(edgeCasesConfigurator, edgeCasesCreator, maxEdgeCases);
	}

	@Override
	public Arbitrary<BigInteger> edgeCases(Consumer<EdgeCases.Config<BigInteger>> configurator) {
		IntegralGeneratingArbitrary clone = typedClone();
		clone.edgeCasesConfigurator = configurator;
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IntegralGeneratingArbitrary that = (IntegralGeneratingArbitrary) o;

		if (!min.equals(that.min)) return false;
		if (!max.equals(that.max)) return false;
		if (!Objects.equals(shrinkingTarget, that.shrinkingTarget)) return false;
		if (!distribution.equals(that.distribution)) return false;
		return LambdaSupport.areEqual(edgeCasesConfigurator, that.edgeCasesConfigurator);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(min, max, shrinkingTarget);
	}

	private Stream<BigInteger> streamDefaultEdgeCases() {
		return streamRawEdgeCases()
			.distinct()
			.filter(aBigInt -> aBigInt.compareTo(min) >= 0 && aBigInt.compareTo(max) <= 0);
	}

	private Stream<BigInteger> streamRawEdgeCases() {
		BigInteger[] literalEdgeCases = new BigInteger[]{
			valueOf(-2), valueOf(-1), BigInteger.ZERO, valueOf(2), valueOf(1),
			min, min.add(ONE), max, max.subtract(ONE)
		};
		return shrinkingTarget == null
				   ? Arrays.stream(literalEdgeCases)
				   : Stream.concat(Stream.of(shrinkingTarget), Arrays.stream(literalEdgeCases));
	}

	private BigInteger shrinkingTarget() {
		if (shrinkingTarget == null) {
			return RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max));
		} else {
			return shrinkingTarget;
		}
	}

	class RangeIterator implements Iterator<BigInteger> {

		BigInteger current = min;

		@Override
		public boolean hasNext() {
			return current.compareTo(max) <= 0;
		}

		@Override
		public BigInteger next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			BigInteger next = current;
			current = current.add(BigInteger.ONE);
			return next;
		}
	}

}
