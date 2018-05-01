package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;

import java.math.*;
import java.util.*;

// TODO: Remove duplication with RandomIntegralGenerators
class RandomDecimalGenerators {

	static RandomGenerator<BigDecimal> bigDecimals(
		BigDecimal min, BigDecimal max, int scale, BigDecimal[] partitionPoints
	) {
		if (scale < 0) {
			throw new JqwikException(String.format("Scale [%s] must be positive.", scale));
		}

		if (min.compareTo(max) == 0) {
			return ignored -> Shrinkable.unshrinkable(min);
		}

		if (min.compareTo(max) > 0) {
			throw new JqwikException(String.format("Min value [%s] must not be greater that max value [%s].", min, max));
		}

		return partitionedGenerator(min, max, scale, partitionPoints);
	}

	private static RandomGenerator<BigDecimal> partitionedGenerator(
		BigDecimal min, BigDecimal max, int scale, BigDecimal[] partitionPoints
	) {
		List<RandomGenerator<BigDecimal>> generators = createPartitions(min, max, scale, partitionPoints);
		if (generators.size() == 1) {
			return generators.get(0);
		}
		return random -> generators.get(random.nextInt(generators.size())).next(random);
	}

	private static List<RandomGenerator<BigDecimal>> createPartitions(
		BigDecimal min, BigDecimal max, int scale, BigDecimal[] partitionPoints
	) {
		List<RandomGenerator<BigDecimal>> partitions = new ArrayList<>();
		Arrays.sort(partitionPoints);
		BigDecimal lower = min;
		for (BigDecimal partitionPoint : partitionPoints) {
			BigDecimal upper = partitionPoint;
			if (upper.compareTo(lower) <= 0) {
				continue;
			}
			if (upper.compareTo(max) >= 0) {
				break;
			}
			partitions.add(createBaseGenerator(lower, upper, scale, new BigDecimalShrinkCandidates(min, max, scale)));
			lower = upper;
		}
		partitions.add(createBaseGenerator(lower, max, scale, new BigDecimalShrinkCandidates(min, max, scale)));
		return partitions;
	}

	private static RandomGenerator<BigDecimal> createBaseGenerator(BigDecimal minGenerate, BigDecimal maxGenerate, int scale, BigDecimalShrinkCandidates candidates) {
		BigInteger scaledMin = minGenerate.scaleByPowerOfTen(scale).toBigInteger();
		BigInteger scaledMax = maxGenerate.scaleByPowerOfTen(scale).toBigInteger();
		return random -> {
			BigInteger randomIntegral = randomIntegral(random, scaledMin, scaledMax);
			BigDecimal randomDecimal = new BigDecimal(randomIntegral, scale);
			return new ShrinkableValue<>(randomDecimal, candidates);
		};
	}

	private static BigInteger randomIntegral(Random random, BigInteger min, BigInteger max) {
		BigInteger range = max.subtract(min);
		int bits = range.bitLength();
		while (true) {
			BigInteger rawValue = new BigInteger(bits, random);
			BigInteger value = rawValue.add(min);
			if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
				return value;
			}
		}
	}

	// TODO: This could be way more sophisticated
	static BigDecimal[] calculateDefaultPartitionPoints(int genSize, BigDecimal min, BigDecimal max) {
		int partitionPoint = Math.max(genSize / 2, 10);
		BigDecimal upperPartitionPoint = BigDecimal.valueOf(partitionPoint).min(max);
		BigDecimal lowerPartitionPoint = BigDecimal.valueOf(partitionPoint).negate().max(min);
		return new BigDecimal[]{lowerPartitionPoint, upperPartitionPoint};
	}
}
