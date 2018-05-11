package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

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

	private RandomGenerator<BigInteger> createGenerator(BigInteger[] partitionPoints, int genSize) {
		List<Shrinkable<BigInteger>> samples =
			Arrays.stream(new BigInteger[]{BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE.negate(), min, max}) //
				  .distinct() //
				  .filter(aBigInt -> aBigInt.compareTo(min) >= 0 && aBigInt.compareTo(max) <= 0) //
				  .map(anInt -> new ShrinkableBigInteger(anInt, Range.of(min, max))) //
				  .collect(Collectors.toList());
		return RandomGenerators.bigIntegers(min, max, partitionPoints).withEdgeCases(genSize, samples);
	}

}
