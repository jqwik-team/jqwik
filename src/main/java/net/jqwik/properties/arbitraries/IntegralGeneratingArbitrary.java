package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

class IntegralGeneratingArbitrary implements Arbitrary<BigInteger> {

	private final BigInteger defaultMin;
	private final BigInteger defaultMax;

	BigInteger min;
	BigInteger max;

	IntegralGeneratingArbitrary(BigInteger defaultMin, BigInteger defaultMax) {
		this.defaultMin = defaultMin;
		this.defaultMax = defaultMax;
		this.min = defaultMin;
		this.max = defaultMax;
	}

	@Override
	public RandomGenerator<BigInteger> generator(int tries) {
		if (min.equals(defaultMin) && max.equals(defaultMax)) {
			BigInteger max = BigInteger.valueOf(Arbitrary.defaultMaxFromTries(tries));
			return longGenerator(max, max);
		}
		return longGenerator(min, max);
	}

	private RandomGenerator<BigInteger> longGenerator(BigInteger minGenerate, BigInteger maxGenerate) {
		BigIntegerShrinkCandidates shrinkCandidates = new BigIntegerShrinkCandidates(min, max);
		List<Shrinkable<BigInteger>> samples =
			Arrays.stream(new BigInteger[]{BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE.negate(), defaultMin, defaultMax, minGenerate, maxGenerate}) //
				  .distinct() //
				  .filter(aBigInt -> aBigInt.compareTo(min) >= 0 && aBigInt.compareTo(max) <= 0) //
				  .map(anInt -> new ShrinkableValue<>(anInt, shrinkCandidates)) //
				  .collect(Collectors.toList());
		return RandomGenerators.choose(minGenerate, maxGenerate).withShrinkableSamples(samples);
	}

}
