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

import static net.jqwik.engine.properties.arbitraries.randomized.RandomDecimalGenerators.*;

class DecimalGeneratingArbitrary extends TypedCloneable implements Arbitrary<BigDecimal> {

	private static final int DEFAULT_SCALE = 2;

	Range<BigDecimal> range;
	int scale = DEFAULT_SCALE;
	BigDecimal shrinkingTarget;
	RandomDistribution distribution = RandomDistribution.biased();

	private Consumer<EdgeCases.Config<BigDecimal>> edgeCasesConfigurator = EdgeCases.Config.noConfig();

	DecimalGeneratingArbitrary(Range<BigDecimal> defaultRange) {
		this.range = defaultRange;
		this.shrinkingTarget = null;
	}

	@Override
	public RandomGenerator<BigDecimal> generator(int genSize) {
		checkRange();
		return RandomDecimalGenerators.bigDecimals(genSize, range, scale, distribution, shrinkingTarget());
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
	public EdgeCases<BigDecimal> edgeCases(int maxEdgeCases) {
		Function<Integer, EdgeCases<BigDecimal>> edgeCasesCreator = max -> EdgeCasesSupport.fromShrinkables(edgeCaseShrinkables(max));
		DecimalEdgeCasesConfiguration configuration = new DecimalEdgeCasesConfiguration(range, scale, shrinkingTarget());
		return configuration.configure(edgeCasesConfigurator, edgeCasesCreator, maxEdgeCases);
	}

	@Override
	public Arbitrary<BigDecimal> edgeCases(Consumer<EdgeCases.Config<BigDecimal>> configurator) {
		DecimalGeneratingArbitrary clone = typedClone();
		clone.edgeCasesConfigurator = configurator;
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DecimalGeneratingArbitrary that = (DecimalGeneratingArbitrary) o;

		if (scale != that.scale) return false;
		if (!range.equals(that.range)) return false;
		if (!Objects.equals(shrinkingTarget, that.shrinkingTarget)) return false;
		if (!Objects.equals(distribution, that.distribution)) return false;
		return Objects.equals(edgeCasesConfigurator, that.edgeCasesConfigurator);
	}

	@Override
	public int hashCode() {
		int result = range.hashCode();
		result = 31 * result + scale;
		return result;
	}

	private List<Shrinkable<BigDecimal>> edgeCaseShrinkables(int maxEdgeCases) {
		Range<BigInteger> bigIntegerRange = unscaledBigIntegerRange(range, scale);
		return streamRawEdgeCases()
				   .filter(aDecimal -> range.includes(aDecimal))
				   .map(value -> {
					   BigInteger bigIntegerValue = unscaledBigInteger(value, scale);
					   BigInteger shrinkingTarget = unscaledBigInteger(shrinkingTarget(), scale);
					   return new ShrinkableBigInteger(bigIntegerValue, bigIntegerRange, shrinkingTarget);
				   })
				   .map(shrinkableBigInteger -> shrinkableBigInteger.map(bigInteger -> scaledBigDecimal(bigInteger, scale)))
				   .limit(Math.max(0, maxEdgeCases))
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
