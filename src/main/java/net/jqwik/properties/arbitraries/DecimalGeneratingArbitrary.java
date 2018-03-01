package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

class DecimalGeneratingArbitrary implements Arbitrary<BigDecimal> {

	private static final int DEFAULT_SCALE = 2;

	BigDecimal min;
	BigDecimal max;
	int scale = DEFAULT_SCALE;

	DecimalGeneratingArbitrary(BigDecimal defaultMin, BigDecimal defaultMax) {
		this.min = defaultMin;
		this.max = defaultMax;
	}

	@Override
	public RandomGenerator<BigDecimal> generator(int tries) {
		BigDecimal[] partitionPoints = RandomDecimalGenerators.calculateDefaultPartitionPoints(tries, this.min, this.max);
		return decimalGenerator(partitionPoints);
	}

	private RandomGenerator<BigDecimal> decimalGenerator(BigDecimal[] partitionPoints) {
		BigDecimalShrinkCandidates shrinkCandidates = new BigDecimalShrinkCandidates(min, max, scale);
		BigDecimal smallest = BigDecimal.ONE.movePointLeft(scale);
		BigDecimal[] sampleValues = {BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE.negate(), smallest, smallest.negate(), min, max};
		List<Shrinkable<BigDecimal>> samples =
			Arrays.stream(sampleValues) //
				  .distinct() //
				  .filter(aDecimal -> aDecimal.compareTo(min) >= 0 && aDecimal.compareTo(max) <= 0) //
				  .map(value -> new ShrinkableValue<>(value, shrinkCandidates)) //
				  .collect(Collectors.toList());
		return RandomGenerators.bigDecimals(min, max, scale, partitionPoints).withShrinkableSamples(samples);
	}

}
