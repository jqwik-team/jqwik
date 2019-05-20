package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

import static java.math.BigInteger.*;

class IntegralGeneratingArbitrary implements Arbitrary<BigInteger> {

	BigInteger min;
	BigInteger max;

	IntegralGeneratingArbitrary(BigInteger defaultMin, BigInteger defaultMax) {
		this.min = defaultMin;
		this.max = defaultMax;
	}

	@Override
	public RandomGenerator<BigInteger> generator(int genSize) {
		BigInteger[] partitionPoints = RandomIntegralGenerators.calculateDefaultPartitionPoints(genSize, this.min, this.max);
		return createGenerator(partitionPoints, genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<BigInteger>> exhaustive() {
		BigInteger maxCount = max.subtract(min).add(BigInteger.ONE);

		if (maxCount.compareTo(valueOf(ExhaustiveGenerators.MAXIMUM_ACCEPTED_MAX_COUNT)) > 0) {
			return Optional.empty();
		} else {
			return ExhaustiveGenerators.fromIterable(RangeIterator::new, maxCount.longValueExact());
		}
	}

	private RandomGenerator<BigInteger> createGenerator(BigInteger[] partitionPoints, int genSize) {
		List<Shrinkable<BigInteger>> edgeCases =
			Arrays.stream(edgeCases()) //
				  .filter(aBigInt -> aBigInt.compareTo(min) >= 0 && aBigInt.compareTo(max) <= 0) //
				  .map(anInt -> new ShrinkableBigInteger(anInt, Range.of(min, max), shrinkingTarget(anInt))) //
				  .collect(Collectors.toList());
		return RandomGenerators.bigIntegers(min, max, partitionPoints).withEdgeCases(genSize, edgeCases);
	}

	private BigInteger shrinkingTarget(BigInteger anInt) {
		return ShrinkableBigInteger.defaultShrinkingTarget(anInt, Range.of(min, max));
	}

	private BigInteger[] edgeCases() {
		return new BigInteger[]{
			valueOf(-10), valueOf(-5), valueOf(-4), valueOf(-3), valueOf(-2), valueOf(-1),
			BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, // more weight for 0
			valueOf(10), valueOf(5), valueOf(4), valueOf(3), valueOf(2), valueOf(1),
			min, max
		};
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
