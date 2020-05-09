package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

public class RandomDecimalGenerators {

	public static RandomGenerator<BigDecimal> bigDecimals(
		int genSize,
		Range<BigDecimal> range,
		int scale,
		RandomDistribution distribution,
		BigDecimal shrinkingTarget
	) {
		checkRangeIsSound(range, scale);

		if (scale < 0) {
			throw new JqwikException(String.format("Scale [%s] must be positive.", scale));
		}

		if (range.isSingular()) {
			return ignored -> Shrinkable.unshrinkable(range.min);
		}

		Range<BigInteger> unscaledRange = unscaledBigIntegerRange(range, scale);
		BigInteger unscaledShrinkingTarget = unscaledBigInteger(shrinkingTarget, scale);
		RandomGenerator<BigInteger> unscaledBigIntegerGenerator =
			RandomIntegralGenerators.bigIntegers(genSize, unscaledRange.min, unscaledRange.max, unscaledShrinkingTarget, distribution);

		return scaledBigDecimalGenerator(unscaledBigIntegerGenerator, scale);
	}

	private static void checkRangeIsSound(Range<BigDecimal> range, int scale) {
		if (range.minIncluded || range.maxIncluded) {
			return;
		}
		BigDecimal minimumDifference = BigDecimal.ONE.movePointLeft(scale);
		if (range.min.add(minimumDifference).compareTo(range.max) >= 0) {
			String message = String.format("No number with scale <%s> can be generated in %s", scale, range);
			throw new JqwikException(message);
		}
	}

	private static RandomGenerator<BigDecimal> scaledBigDecimalGenerator(
		final RandomGenerator<BigInteger> scaledBigIntegerGenerator,
		final int scale
	) {
		return scaledBigIntegerGenerator.map(value -> scaledBigDecimal(value, scale));
	}

	public static Range<BigInteger> unscaledBigIntegerRange(final Range<BigDecimal> range, final int scale) {
		BigInteger minScaled = range.minIncluded ? unscaledBigInteger(range.min, scale) : unscaledBigInteger(range.min, scale)
																							  .add(BigInteger.ONE);
		BigInteger maxScaled = range.maxIncluded ? unscaledBigInteger(range.max, scale) : unscaledBigInteger(range.max, scale)
																							  .subtract(BigInteger.ONE);
		return Range.of(minScaled, true, maxScaled, true);
	}

	public static BigDecimal scaledBigDecimal(final BigInteger value, final int scale) {
		return new BigDecimal(value, scale);
	}

	public static BigInteger unscaledBigInteger(final BigDecimal bigDecimal, final int scale) {
		return bigDecimal.setScale(scale).unscaledValue();
	}

	public static BigDecimal defaultShrinkingTarget(Range<BigDecimal> range, int scale) {
		if (range.includes(BigDecimal.ZERO))
			return BigDecimal.ZERO;
		else {
			if (range.max.compareTo(BigDecimal.ZERO) <= 0) {
				if (range.maxIncluded) {
					return range.max;
				} else {
					BigDecimal minimumDifference = BigDecimal.ONE.movePointLeft(scale);
					return range.max.subtract(minimumDifference);
				}
			}
			if (range.min.compareTo(BigDecimal.ZERO) >= 0) {
				if (range.minIncluded) {
					return range.min;
				} else {
					BigDecimal minimumDifference = BigDecimal.ONE.movePointLeft(scale);
					return range.min.add(minimumDifference);
				}
			}
		}
		throw new RuntimeException("This should not be possible");
	}

}
