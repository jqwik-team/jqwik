package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.shrinking.*;

// TODO: Remove duplication with RandomIntegralGenerators
public class RandomDecimalGenerators {

	public static RandomGenerator<BigDecimal> bigDecimals(Range<BigDecimal> range, int scale, BigDecimal[] partitionPoints) {
		if (scale < 0) {
			throw new JqwikException(String.format("Scale [%s] must be positive.", scale));
		}

		if (range.isSingular()) {
			return ignored -> Shrinkable.unshrinkable(range.min);
		}

		return partitionedGenerator(range, scale, partitionPoints);
	}

	private static RandomGenerator<BigDecimal> partitionedGenerator(Range<BigDecimal> range, int scale, BigDecimal[] partitionPoints) {
		List<RandomGenerator<BigDecimal>> generators = createPartitions(range, scale, partitionPoints);
		if (generators.size() == 1) {
			return generators.get(0);
		}
		return random -> generators.get(random.nextInt(generators.size())).next(random);
	}

	private static List<RandomGenerator<BigDecimal>> createPartitions(Range<BigDecimal> range, int scale, BigDecimal[] partitionPoints) {
		List<RandomGenerator<BigDecimal>> partitions = new ArrayList<>();
		Arrays.sort(partitionPoints);
		BigDecimal lower = range.min;
		for (BigDecimal partitionPoint : partitionPoints) {
			BigDecimal upper = partitionPoint;
			if (upper.compareTo(lower) <= 0) {
				continue;
			}
			if (upper.compareTo(range.max) >= 0) {
				break;
			}
			partitions.add(createBaseGenerator(lower, upper, scale, range));
			lower = upper;
		}
		partitions.add(createBaseGenerator(lower, range.max, scale, range));
		return partitions;
	}

	private static RandomGenerator<BigDecimal> createBaseGenerator(BigDecimal minGenerate, BigDecimal maxGenerate, int scale, Range<BigDecimal> range) {
		BigInteger scaledMinTry = minGenerate.scaleByPowerOfTen(scale).toBigInteger();
		BigInteger scaledMin = new BigDecimal(scaledMinTry, scale).compareTo(minGenerate) >= 0 ?
			scaledMinTry : scaledMinTry.add(BigInteger.ONE);
		BigInteger scaledMaxTry = maxGenerate.scaleByPowerOfTen(scale).toBigInteger();
		BigInteger scaledMax = new BigDecimal(scaledMaxTry, scale).compareTo(maxGenerate) <= 0 ?
			scaledMaxTry : scaledMaxTry.subtract(BigInteger.ONE);
		return random -> {
			if (scaledMin.compareTo(scaledMax) >= 0) {
				return new ShrinkableBigDecimal(minGenerate, range, scale);
			}
			BigInteger randomIntegral = randomIntegral(random, scaledMin, scaledMax);
			BigDecimal randomDecimal = new BigDecimal(randomIntegral, scale);
			return new ShrinkableBigDecimal(randomDecimal, range, scale);
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
	public static BigDecimal[] calculateDefaultPartitionPoints(int genSize, BigDecimal min, BigDecimal max) {
		int partitionPoint = Math.max(genSize / 2, 10);
		BigDecimal upperPartitionPoint = BigDecimal.valueOf(partitionPoint).min(max);
		BigDecimal lowerPartitionPoint = BigDecimal.valueOf(partitionPoint).negate().max(min);
		return new BigDecimal[]{lowerPartitionPoint, upperPartitionPoint};
	}
}
