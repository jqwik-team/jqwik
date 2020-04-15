package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;
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

	IntegralGeneratingArbitrary(BigInteger defaultMin, BigInteger defaultMax) {
		this.min = defaultMin;
		this.max = defaultMax;
		this.shrinkingTarget = null;
	}

	@Override
	public RandomGenerator<BigInteger> generator(int genSize) {
		BigInteger[] partitionPoints = RandomGenerators.calculateDefaultPartitionPoints(genSize, this.min, this.max);
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
		return EdgeCases.fromStream(streamEdgeCaseShrinkables());
//		return () -> streamEdgeCaseShrinkables().iterator();
	}

	private RandomGenerator<BigInteger> createGenerator(BigInteger[] partitionPoints, int genSize) {
		List<Shrinkable<BigInteger>> edgeCases = streamEdgeCaseShrinkables().collect(Collectors.toList());
		return RandomGenerators.bigIntegers(min, max, shrinkingTargetCalculator(), partitionPoints)
							   .withEdgeCases(genSize, edgeCases);
	}

	private Stream<Shrinkable<BigInteger>> streamEdgeCaseShrinkables() {
		return streamEdgeCases()
				   .filter(aBigInt -> aBigInt.compareTo(min) >= 0 && aBigInt.compareTo(max) <= 0) //
				   .map(anInt -> new ShrinkableBigInteger(anInt, Range.of(min, max), shrinkingTarget(anInt)));
	}

	private Stream<BigInteger> streamEdgeCases() {
		BigInteger[] literalEdgeCases = new BigInteger[]{
			valueOf(-10),
//			valueOf(-5), valueOf(-4), valueOf(-3),
			valueOf(-2), valueOf(-1),
			BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, // more weight for 0
			valueOf(10),
//			valueOf(5), valueOf(4), valueOf(3),
			valueOf(2), valueOf(1),
			min, max
		};
		return shrinkingTarget == null
				   ? Arrays.stream(literalEdgeCases)
				   : Stream.concat(Stream.of(shrinkingTarget), Arrays.stream(literalEdgeCases));
	}

	private Function<BigInteger, BigInteger> shrinkingTargetCalculator() {
		if (shrinkingTarget == null) {
			return RandomGenerators.defaultShrinkingTargetCalculator(min, max);
		} else {
			return ignore -> shrinkingTarget;
		}
	}

	private BigInteger shrinkingTarget(BigInteger anInt) {
		return shrinkingTargetCalculator().apply(anInt);
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
