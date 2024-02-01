package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;

import net.jqwik.api.*;

public class UniformRandomDistribution implements RandomDistribution {

	@Override
	public RandomNumericGenerator createGenerator(
		int genSize,
		BigInteger min,
		BigInteger max,
		BigInteger center
	) {
		// Small number generation can be faster
		if (isWithinIntegerRange(min, max)) {
			return new SmallUniformNumericGenerator(min, max);
		} else {
			return new BigUniformNumericGenerator(min, max);
		}

	}

	private static boolean isWithinIntegerRange(BigInteger min, BigInteger max) {
		boolean rangeIsSmallerThanIntegerMax = max.subtract(min).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0;
		boolean minAndMaxAreWithinInt = min.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0
						&& max.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0;
		return rangeIsSmallerThanIntegerMax && minAndMaxAreWithinInt;
	}

	@Override
	public String toString() {
		return "UniformDistribution";
	}

}
