package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;

import java.math.*;
import java.util.*;

// TODO: Remove duplication with RandomDecimalGenerators
class RandomIntegralGenerators {

	static RandomGenerator<BigInteger> bigIntegers(BigInteger min, BigInteger max, BigInteger[] partitionPoints) {
		if (min.compareTo(max) == 0) {
			return ignored -> Shrinkable.unshrinkable(min);
		}

		if (min.compareTo(max) > 0) {
			throw new JqwikException(String.format("Min value [%s] must not be greater that max value [%s].", min, max));
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
			partitions.add(createBaseGenerator(lower, upper.subtract(BigInteger.ONE), new BigIntegerShrinkCandidates(min, max)));
			lower = upper;
		}
		partitions.add(createBaseGenerator(lower, max, new BigIntegerShrinkCandidates(min, max)));
		return partitions;
	}

	private static RandomGenerator<BigInteger> createBaseGenerator(BigInteger min, BigInteger max, BigIntegerShrinkCandidates shrinkCandidates) {
		if (isWithinIntegerRange(min, max)) {
			return createIntegerGenerator(min, max, shrinkCandidates);
		} else {
			return createBigIntegerGenerator(min, max, shrinkCandidates);
		}
	}

	private static RandomGenerator<BigInteger> createBigIntegerGenerator(BigInteger min, BigInteger max, BigIntegerShrinkCandidates shrinkCandidates) {
		BigInteger range = max.subtract(min);
		int bits = range.bitLength();
		return random -> {
			while (true) {
				BigInteger rawValue = new BigInteger(bits, random);
				BigInteger value = rawValue.add(min);
				if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
					return new ShrinkableValue<>(value, shrinkCandidates);
				}
			}
		};
	}

	private static RandomGenerator<BigInteger> createIntegerGenerator(BigInteger min, BigInteger max, BigIntegerShrinkCandidates shrinkCandidates) {
		final int _min = Math.min(min.intValue(), max.intValue());
		final int _max = Math.max(min.intValue(), max.intValue());
		return random -> {
			int bound = Math.abs(_max - _min) + 1;
			int value = random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + _min;
			return new ShrinkableValue<>(BigInteger.valueOf(value), shrinkCandidates);
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
