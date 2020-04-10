package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

class DecimalGeneratingArbitrary implements Arbitrary<BigDecimal> {

	private static final int DEFAULT_SCALE = 2;

	//Range<BigDecimal> range;
	BigDecimal min;
	BigDecimal max;
	int scale = DEFAULT_SCALE;
	BigDecimal shrinkingTarget;

	DecimalGeneratingArbitrary(Range<BigDecimal> defaultRange) {
		//this.range = defaultRange;
		this.min = defaultRange.min;
		this.max = defaultRange.max;
		this.shrinkingTarget = null;
	}

	@Override
	public RandomGenerator<BigDecimal> generator(int genSize) {
		BigDecimal[] partitionPoints = RandomGenerators.calculateDefaultPartitionPoints(genSize, getRange());
		return decimalGenerator(partitionPoints, genSize);
	}

	public Range<BigDecimal> getRange() {
		return Range.of(this.min, this.max);
	}

	@Override
	public Optional<ExhaustiveGenerator<BigDecimal>> exhaustive(long maxNumberOfSamples) {
		if (min.compareTo(max) == 0) {
			return ExhaustiveGenerators.choose(Collections.singletonList(min), maxNumberOfSamples);
		}
		return Optional.empty();
	}

	private RandomGenerator<BigDecimal> decimalGenerator(BigDecimal[] partitionPoints, int genSize) {
		List<Shrinkable<BigDecimal>> edgeCases =
			streamEdgeCases() //
							  .filter(aDecimal -> aDecimal.compareTo(min) >= 0 && aDecimal.compareTo(max) <= 0) //
							  .map(value -> new ShrinkableBigDecimal(value, getRange(), scale, shrinkingTarget(value))) //
							  .collect(Collectors.toList());
		return RandomGenerators.bigDecimals(getRange(), scale, shrinkingTargetCalculator(), partitionPoints)
							   .withEdgeCases(genSize, edgeCases);
	}

	private Stream<BigDecimal> streamEdgeCases() {
		BigDecimal smallest = BigDecimal.ONE.movePointLeft(scale);
		BigDecimal zeroScaled = BigDecimal.ZERO.movePointLeft(scale);
		BigDecimal[] literalEdgeCases = {
			zeroScaled, zeroScaled, zeroScaled, // raise probability for zero
			BigDecimal.ONE, BigDecimal.ONE.negate(),
			smallest, smallest.negate(), min, max};

		return shrinkingTarget == null
			? Arrays.stream(literalEdgeCases)
			: Stream.concat(Stream.of(shrinkingTarget), Arrays.stream(literalEdgeCases));
	}

	private Function<BigDecimal, BigDecimal> shrinkingTargetCalculator() {
		if (shrinkingTarget == null) {
			return RandomGenerators.defaultShrinkingTargetCalculator(getRange());
		} else {
			return ignore -> shrinkingTarget;
		}
	}

	private BigDecimal shrinkingTarget(BigDecimal aDecimal) {
		return shrinkingTargetCalculator().apply(aDecimal);
	}


}
