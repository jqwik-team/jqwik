package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

public class RandomDecimalGenerators {

	public static RandomGenerator<BigDecimal> bigDecimals(
		Range<BigDecimal> range,
		int scale,
		BigDecimal[] partitionPoints,
		Function<BigDecimal, BigDecimal> shrinkingTargetCalculator
	) {
		if (scale < 0) {
			throw new JqwikException(String.format("Scale [%s] must be positive.", scale));
		}

		if (range.isSingular()) {
			return ignored -> Shrinkable.unshrinkable(range.min);
		}

		Range<BigInteger> scaledRange = scaleToBigIntegerRange(range, scale);
		BigInteger[] scaledPartitionPoints = scaleToBigIntegerPartitions(partitionPoints, scale);
		Function<BigInteger, BigInteger> scaledTargetCalculator = value -> {
			BigDecimal bigDecimalResult = shrinkingTargetCalculator.apply(unscaleFromBigInteger(value, scale));
			return scaleToBigInteger(bigDecimalResult, scale);
		};
		RandomGenerator<BigInteger> scaledBigIntegerGenerator =
			RandomIntegralGenerators.bigIntegers(scaledRange, scaledPartitionPoints, scaledTargetCalculator);

		return unscaleFromBigIntegerGenerator(scaledBigIntegerGenerator, scale);
	}

	private static BigInteger[] scaleToBigIntegerPartitions(final BigDecimal[] partitionPoints, final int scale) {
		return Arrays.stream(partitionPoints).map(bigDecimal -> scaleToBigInteger(bigDecimal, scale))
					 .toArray(BigInteger[]::new);
	}

	private static RandomGenerator<BigDecimal> unscaleFromBigIntegerGenerator(
		final RandomGenerator<BigInteger> scaledBigIntegerGenerator,
		final int scale
	) {
		return scaledBigIntegerGenerator.map(value -> unscaleFromBigInteger(value, scale));
	}

	public static Range<BigInteger> scaleToBigIntegerRange(final Range<BigDecimal> range, final int scale) {
		BigInteger minScaled = range.minIncluded ? scaleToBigInteger(range.min, scale) : scaleToBigInteger(range.min, scale).add(BigInteger.ONE);
		BigInteger maxScaled = range.maxIncluded ? scaleToBigInteger(range.max, scale) : scaleToBigInteger(range.max, scale).subtract(BigInteger.ONE);
		return Range.of(minScaled, true, maxScaled, true);
	}

	public static BigDecimal unscaleFromBigInteger(final BigInteger value, final int scale) {
		return new BigDecimal(value, scale);
	}

	public static BigInteger scaleToBigInteger(final BigDecimal bigDecimal, final int scale) {
		return bigDecimal.setScale(scale).unscaledValue();
	}

	public static BigDecimal defaultShrinkingTarget(BigDecimal value, Range<BigDecimal> range, int scale) {
		if (range.includes(BigDecimal.ZERO))
			return BigDecimal.ZERO;
		else {
			if (value.compareTo(BigDecimal.ZERO) < 0) {
				if (range.maxIncluded) {
					return range.max;
				} else {
					BigDecimal minimumDifference = BigDecimal.ONE.movePointLeft(scale);
					return range.max.subtract(minimumDifference);
				}
			}
			if (value.compareTo(BigDecimal.ZERO) > 0) {
				if (range.minIncluded) {
					return range.min;
				} else {
					BigDecimal minimumDifference = BigDecimal.ONE.movePointLeft(scale);
					return range.min.add(minimumDifference);
				}
			}
		}
		return value; // Should never get here
	}

}
