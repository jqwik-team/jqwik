package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

import static net.jqwik.engine.properties.arbitraries.randomized.RandomDecimalGenerators.*;

class DecimalGeneratingArbitrary implements Arbitrary<BigDecimal> {

	private static final int DEFAULT_SCALE = 2;

	Range<BigDecimal> range;
	int scale = DEFAULT_SCALE;
	BigDecimal shrinkingTarget;
	RandomDistribution distribution = RandomDistribution.biased();

	DecimalGeneratingArbitrary(Range<BigDecimal> defaultRange) {
		this.range = defaultRange;
		this.shrinkingTarget = null;
	}

	@Override
	public RandomGenerator<BigDecimal> generator(int genSize) {
		checkRange();
		return RandomDecimalGenerators
			.bigDecimals(genSize, range, scale, distribution, shrinkingTarget())
			.withEdgeCases(genSize, edgeCases());
	}

	private void checkRange() {
		checkScale(range.min);
		checkScale(range.max);
	}

	private void checkScale(final BigDecimal value) {
		try {
			value.setScale(scale);
		} catch (ArithmeticException arithmeticException) {
			String message = String.format(
				"Decimal value %s cannot be represented with scale %s.%nYou may want to use a higher scale",
				value,
				scale
			);
			throw new JqwikException(message);
		}
	}

	@Override
	public Optional<ExhaustiveGenerator<BigDecimal>> exhaustive(long maxNumberOfSamples) {
		if (range.isSingular()) {
			return ExhaustiveGenerators.choose(Collections.singletonList(range.min), maxNumberOfSamples);
		}
		return Optional.empty();
	}

	@Override
	public EdgeCases<BigDecimal> edgeCases() {
		return EdgeCasesSupport.fromShrinkables(edgeCaseShrinkables());
	}

	private List<Shrinkable<BigDecimal>> edgeCaseShrinkables() {
		Range<BigInteger> bigIntegerRange = unscaledBigIntegerRange(range, scale);
		return streamRawEdgeCases()
				   .filter(aDecimal -> range.includes(aDecimal))
				   .map(value -> {
					   BigInteger bigIntegerValue = unscaledBigInteger(value, scale);
					   BigInteger shrinkingTarget = unscaledBigInteger(shrinkingTarget(), scale);
					   return new ShrinkableBigInteger(bigIntegerValue, bigIntegerRange, shrinkingTarget);
				   })
				   .map(shrinkableBigInteger -> shrinkableBigInteger.map(bigInteger -> scaledBigDecimal(bigInteger, scale)))
				   .collect(Collectors.toList());
	}

	private Stream<BigDecimal> streamRawEdgeCases() {
		BigDecimal smallest = BigDecimal.ONE.movePointLeft(scale);
		BigDecimal minBorder = range.minIncluded ? range.min : range.min.add(smallest);
		BigDecimal maxBorder = range.maxIncluded ? range.max : range.max.subtract(smallest);
		BigDecimal[] literalEdgeCases = {
			BigDecimal.ZERO.movePointLeft(scale),
			BigDecimal.ONE, BigDecimal.ONE.negate(),
			smallest, smallest.negate(),
			minBorder, maxBorder
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

	private BigDecimal shrinkingTarget() {
		if (shrinkingTarget == null) {
			return RandomDecimalGenerators.defaultShrinkingTarget(range, scale);
		} else {
			return shrinkingTarget;
		}
	}
}
