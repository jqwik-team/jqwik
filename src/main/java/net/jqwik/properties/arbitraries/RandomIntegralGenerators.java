package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;

import java.math.*;
import java.util.*;

// TODO: Remove duplication with RandomDecimalGenerators
class RandomIntegralGenerators {

	// TODO: Create BigIntegers even if outside Long range
	static RandomGenerator<BigInteger> bigIntegers(BigInteger min, BigInteger max, BigInteger[] partitionPoints) {
		if (min.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0)
			throw new JqwikException("Cannot create numbers < Long.MIN_VALUE");
		if (min.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0)
			throw new JqwikException("Cannot create numbers > Long.MAX_VALUE");

		if (min.equals(max)) {
			return ignored -> Shrinkable.unshrinkable(min);
		}

		return partitionedGenerator(min, max, partitionPoints);
	}

	private static RandomGenerator<BigInteger> partitionedGenerator(
		BigInteger min, BigInteger max, BigInteger[] partitionPoints
	) {
		List<RandomGenerator<BigInteger>> generators = createPartitions(min, max, partitionPoints);
		if (generators.size() == 1) {
			return generators.get(0);
		}
		return random -> generators.get(random.nextInt(generators.size())).next(random);
	}

	private static List<RandomGenerator<BigInteger>> createPartitions(
		BigInteger min, BigInteger max, BigInteger[] partitionPoints
	) {
		List<RandomGenerator<BigInteger>> partitions = new ArrayList<>();
		Arrays.sort(partitionPoints);
		BigInteger lower = min;
		for (BigInteger partitionPoint : partitionPoints) {
			BigInteger upper = partitionPoint;
			if (upper.compareTo(lower) <= 0) {
				continue;
			}
			if (upper.compareTo(max) >= 0) {
				break;
			}
			partitions.add(createBaseGenerator(lower, upper.subtract(BigInteger.ONE)));
			lower = upper;
		}
		partitions.add(createBaseGenerator(lower, max));
		return partitions;
	}

	private static RandomGenerator<BigInteger> createBaseGenerator(BigInteger min, BigInteger max) {
		if (isWithinIntegerRange(min, max)) {
			return createIntegerGenerator(min, max);
		} else {
			return createBigIntegerGenerator(min, max);
		}
	}

	private static RandomGenerator<BigInteger> createBigIntegerGenerator(BigInteger min, BigInteger max) {
		final long _min = Math.min(min.longValue(), max.longValue());
		final long _max = Math.max(min.longValue(), max.longValue());
		return random -> {
			final double d = random.nextDouble();
			long value = (long) ((d * _max) + ((1.0 - d) * _min) + d);
			return new ShrinkableValue<>(BigInteger.valueOf(value), new BigIntegerShrinkCandidates(min, max));
		};
	}

	private static RandomGenerator<BigInteger> createIntegerGenerator(BigInteger min, BigInteger max) {
		final int _min = Math.min(min.intValue(), max.intValue());
		final int _max = Math.max(min.intValue(), max.intValue());
		return random -> {
			int bound = Math.abs(_max - _min) + 1;
			int value = random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + _min;
			return new ShrinkableValue<>(BigInteger.valueOf(value), new BigIntegerShrinkCandidates(min, max));
		};
	}

	private static boolean isWithinIntegerRange(BigInteger min, BigInteger max) {
		return min.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0
			&& max.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0;
	}

	// TODO: This could be way more sophisticated
	static BigInteger[] calculateDefaultPartitionPoints(int tries, BigInteger min, BigInteger max) {
		int partitionPoint = Math.max(tries / 2, 10);
		BigInteger upperPartitionPoint = BigInteger.valueOf(partitionPoint).min(max);
		BigInteger lowerPartitionPoint = BigInteger.valueOf(partitionPoint).negate().max(min);
		return new BigInteger[]{lowerPartitionPoint, upperPartitionPoint};
	}
}
