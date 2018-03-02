package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.math.*;
import java.util.*;

// TODO: Remove duplication with RandomIntegralGenerators
class RandomDecimalGenerators {

	static RandomGenerator<BigDecimal> bigDecimals(
		BigDecimal min, BigDecimal max, int scale, BigDecimal[] partitionPoints
	) {
		if (min.compareTo(max) >= 0) {
			return ignored -> Shrinkable.unshrinkable(min);
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
			partitions.add(createBaseGenerator(lower, upper, scale));
			lower = upper;
		}
		partitions.add(createBaseGenerator(lower, max, scale));
		return partitions;
	}

	private static RandomGenerator<BigDecimal> createBaseGenerator(BigDecimal min, BigDecimal max, int scale) {
		return random -> {
			BigDecimal randomDecimal = RandomDecimalGenerators.randomDecimal(random, min, max, scale);
			return new ShrinkableValue<>(randomDecimal, new BigDecimalShrinkCandidates(min, max, scale));
		};
	}

	/**
	 * Random decimal are not equally distributed but randomly scaled down towards 0. Thus random decimals are more likely
	 * to be closer to 0 than
	 *
	 * @param random
	 *            source of randomness
	 * @param min
	 *            lower bound (included) of value to generate
	 * @param max
	 *            upper bound (included) of value to generate
	 * @param precision
	 *            The number of decimals to the right of decimal point
	 *
	 * @return a generated instance of BigDecimal
	 */
	private static BigDecimal randomDecimal(Random random, BigDecimal min, BigDecimal max, int precision) {
		BigDecimal range = max.subtract(min);
		BigDecimal randomFactor = new BigDecimal(random.nextDouble());
		BigDecimal unscaledRandom = randomFactor.multiply(range).add(min);
		int digits = Math.max(1, unscaledRandom.precision() - unscaledRandom.scale());
		int randomScaleDown = random.nextInt(digits);
		BigDecimal scaledRandom = unscaledRandom.movePointLeft(randomScaleDown);
		return scaledRandom.setScale(precision, BigDecimal.ROUND_DOWN);
	}

	// TODO: This could be way more sophisticated
	static BigDecimal[] calculateDefaultPartitionPoints(int tries, BigDecimal min, BigDecimal max) {
		int partitionPoint = Math.max(tries / 2, 10);
		BigDecimal upperPartitionPoint = BigDecimal.valueOf(partitionPoint).min(max);
		BigDecimal lowerPartitionPoint = BigDecimal.valueOf(partitionPoint).negate().max(min);
		return new BigDecimal[]{lowerPartitionPoint, upperPartitionPoint};
	}
}
