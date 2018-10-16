package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.exhaustive.*;
import net.jqwik.properties.arbitraries.randomized.*;
import net.jqwik.properties.shrinking.*;

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

		if (maxCount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
			return Optional.empty();
		} else {
			return ExhaustiveGenerators.fromIterable(RangeIterator::new, maxCount.longValueExact());
		}
	}

	private RandomGenerator<BigInteger> createGenerator(BigInteger[] partitionPoints, int genSize) {
		List<Shrinkable<BigInteger>> samples =
			Arrays.stream(new BigInteger[]{BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE.negate(), min, max}) //
				  .distinct() //
				  .filter(aBigInt -> aBigInt.compareTo(min) >= 0 && aBigInt.compareTo(max) <= 0) //
				  .map(anInt -> new ShrinkableBigInteger(anInt, Range.of(min, max))) //
				  .collect(Collectors.toList());
		return RandomGenerators.bigIntegers(min, max, partitionPoints).withEdgeCases(genSize, samples);
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
