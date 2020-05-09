package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

import static java.math.BigInteger.*;

class IntegralGeneratingArbitrary implements Arbitrary<BigInteger> {

	BigInteger min;
	BigInteger max;
	BigInteger shrinkingTarget;
	RandomDistribution distribution = RandomDistribution.biased();

	IntegralGeneratingArbitrary(BigInteger defaultMin, BigInteger defaultMax) {
		this.min = defaultMin;
		this.max = defaultMax;
		this.shrinkingTarget = null;
	}

	@Override
	public RandomGenerator<BigInteger> generator(int genSize) {
		List<BigInteger> partitionPoints = RandomGenerators.calculatePartitionPoints(distribution, genSize, this.min, this.max, shrinkingTarget());
		return createGenerator(partitionPoints, genSize);
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
	public EdgeCases<BigInteger> edgeCases() {
		List<Shrinkable<BigInteger>> shrinkables =
			streamEdgeCases()
				.map(value -> new ShrinkableBigInteger(
					value,
					Range.of(min, max),
					shrinkingTarget()
				))
				.collect(Collectors.toList());
		return EdgeCases.fromShrinkables(shrinkables);
	}

	private RandomGenerator<BigInteger> createGenerator(List<BigInteger> partitionPoints, int genSize) {
		return RandomGenerators.bigIntegers(min, max, shrinkingTarget(), partitionPoints)
							   .withEdgeCases(genSize, edgeCases());
	}

	private Stream<BigInteger> streamEdgeCases() {
		return streamRawEdgeCases()
				   .distinct()
				   .filter(aBigInt -> aBigInt.compareTo(min) >= 0 && aBigInt.compareTo(max) <= 0);
	}

	private Stream<BigInteger> streamRawEdgeCases() {
		BigInteger[] literalEdgeCases = new BigInteger[]{
			valueOf(-2), valueOf(-1), BigInteger.ZERO, valueOf(2), valueOf(1),
			min, max
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
