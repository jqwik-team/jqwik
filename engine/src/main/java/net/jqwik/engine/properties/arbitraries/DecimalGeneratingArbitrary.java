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

class DecimalGeneratingArbitrary implements Arbitrary<BigDecimal> {

	private static final int DEFAULT_SCALE = 2;

	Range<BigDecimal> range;
	int scale = DEFAULT_SCALE;
	BigDecimal shrinkingTarget;

	DecimalGeneratingArbitrary(Range<BigDecimal> defaultRange) {
		this.range = defaultRange;
		this.shrinkingTarget = null;
	}

	@Override
	public RandomGenerator<BigDecimal> generator(int genSize) {
		BigDecimal[] partitionPoints = RandomGenerators.calculateDefaultPartitionPoints(genSize, range);
		return decimalGenerator(partitionPoints, genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<BigDecimal>> exhaustive(long maxNumberOfSamples) {
		if (range.isSingular()) {
			return ExhaustiveGenerators.choose(Collections.singletonList(range.min), maxNumberOfSamples);
		}
		return Optional.empty();
	}

	private RandomGenerator<BigDecimal> decimalGenerator(BigDecimal[] partitionPoints, int genSize) {
		List<Shrinkable<BigDecimal>> edgeCases =
			streamEdgeCases()
				.map(value -> new ShrinkableBigDecimal(value, range, scale, shrinkingTarget(value)))
				.collect(Collectors.toList());
		return RandomGenerators.bigDecimals(range, scale, shrinkingTargetCalculator(), partitionPoints)
							   .withEdgeCases(genSize, edgeCases);
	}

	private Stream<BigDecimal> streamEdgeCases() {
		return streamRawEdgeCases()
			.filter(aDecimal -> range.includes(aDecimal));
	}

	private Stream<BigDecimal> streamRawEdgeCases() {
		BigDecimal smallest = BigDecimal.ONE.movePointLeft(scale);
		BigDecimal[] literalEdgeCases = {
			BigDecimal.ZERO.movePointLeft(scale),
			BigDecimal.ONE, BigDecimal.ONE.negate(),
			smallest, smallest.negate(),
			range.min, range.max
		};

		if (shrinkingTarget == null) {
			return Arrays.stream(literalEdgeCases);
		} else {
			return Stream.concat(
				Stream.of(shrinkingTarget, shrinkingTarget.add(smallest), shrinkingTarget.subtract(smallest)),
				Arrays.stream(literalEdgeCases)
			);
		}
	}

	private Function<BigDecimal, BigDecimal> shrinkingTargetCalculator() {
		if (shrinkingTarget == null) {
			return RandomGenerators.defaultShrinkingTargetCalculator(range, scale);
		} else {
			return ignore -> shrinkingTarget;
		}
	}

	private BigDecimal shrinkingTarget(BigDecimal aDecimal) {
		return shrinkingTargetCalculator().apply(aDecimal);
	}

}
