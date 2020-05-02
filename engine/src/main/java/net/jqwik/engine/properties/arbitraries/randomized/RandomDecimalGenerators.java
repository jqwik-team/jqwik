package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

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

		BigInteger minScaled = range.minIncluded ? scale(range.min, scale) : scale(range.min, scale).add(BigInteger.ONE);
		BigInteger maxScaled = range.maxIncluded ? scale(range.max, scale) : scale(range.max, scale).subtract(BigInteger.ONE);
		Range<BigInteger> scaledRange = Range.of(minScaled, true, maxScaled, true);
		BigInteger[] scaledPartitionPoints = Arrays.stream(partitionPoints).map(bigDecimal -> scale(bigDecimal, scale))
												   .toArray(BigInteger[]::new);
		Function<BigInteger, BigInteger> scaledTargetCalculator = value -> {
			BigDecimal bigDecimalResult = shrinkingTargetCalculator.apply(unScale(value, scale));
			return scale(bigDecimalResult, scale);
		};
		RandomGenerator<BigInteger> scaledBigIntegerGenerator =
			RandomIntegralGenerators.bigIntegers(scaledRange, scaledPartitionPoints, scaledTargetCalculator);

		return scaledBigIntegerGenerator.map(value -> unScale(value, scale));
	}

	private static BigDecimal unScale(final BigInteger value, final int scale) {
		return new BigDecimal(value, scale);
	}

	private static BigInteger scale(final BigDecimal bigDecimal, final int scale) {
		return bigDecimal.movePointRight(scale).toBigInteger();
	}

}
