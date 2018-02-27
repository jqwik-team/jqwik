package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

class DecimalGeneratingArbitrary implements Arbitrary<BigDecimal> {

	private static final int DEFAULT_SCALE = 2;

	private final BigDecimal defaultMin;
	private final BigDecimal defaultMax;

	BigDecimal min;
	BigDecimal max;
	int scale = DEFAULT_SCALE;

	DecimalGeneratingArbitrary(BigDecimal defaultMin, BigDecimal defaultMax) {
		this.defaultMin = defaultMin;
		this.defaultMax = defaultMax;
		this.min = defaultMin;
		this.max = defaultMax;
	}

	@Override
	public RandomGenerator<BigDecimal> generator(int tries) {
		if (min.equals(defaultMin) && max.equals(defaultMax)) {
			BigDecimal max = new BigDecimal(Arbitrary.defaultMaxFromTries(tries));
			return decimalGenerator(max.negate(), max, scale);
		}
		return decimalGenerator(min, max, scale);
	}

	private RandomGenerator<BigDecimal> decimalGenerator(BigDecimal minGenerate, BigDecimal maxGenerate, int scale) {
		BigDecimalShrinkCandidates decimalShrinkCandidates = new BigDecimalShrinkCandidates(min, max, scale);
		BigDecimal smallest = BigDecimal.ONE.movePointLeft(scale);
		BigDecimal[] sampleValues = { BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE.negate(), smallest, smallest.negate(), max, min,
				minGenerate, maxGenerate };
		List<Shrinkable<BigDecimal>> samples = Arrays.stream(sampleValues) //
				.distinct() //
				.filter(aDecimal -> aDecimal.compareTo(min) >= 0 && aDecimal.compareTo(max) <= 0) //
				.map(value -> new ShrinkableValue<>(value, decimalShrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.bigDecimals(minGenerate, maxGenerate, scale).withShrinkableSamples(samples);
	}

}
