package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.randomized.*;
import net.jqwik.properties.shrinking.*;

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
	public RandomGenerator<BigDecimal> generator(int genSize) {
		BigDecimal[] partitionPoints = RandomDecimalGenerators.calculateDefaultPartitionPoints(genSize, this.min, this.max);
		return decimalGenerator(partitionPoints, genSize);
	}

	private RandomGenerator<BigDecimal> decimalGenerator(BigDecimal[] partitionPoints, int genSize) {
		BigDecimal smallest = BigDecimal.ONE.movePointLeft(scale);
		BigDecimal[] sampleValues = {BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE.negate(), smallest, smallest.negate(), min, max};
		List<Shrinkable<BigDecimal>> samples =
			Arrays.stream(sampleValues) //
				  .distinct() //
				  .filter(aDecimal -> aDecimal.compareTo(min) >= 0 && aDecimal.compareTo(max) <= 0) //
				  .map(value -> new ShrinkableBigDecimal(value, Range.of(min, max), scale)) //
				  .collect(Collectors.toList());
		return RandomGenerators.bigDecimals(min, max, scale, partitionPoints).withEdgeCases(genSize, samples);
	}

}
