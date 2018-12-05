package net.jqwik.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.properties.shrinking.*;

// TODO: Remove duplication with RandomDecimalGenerators
public class RandomIntegralGenerators {

	public static RandomGenerator<BigInteger> bigIntegers(Range<BigInteger> range, BigInteger[] partitionPoints) {
		if (range.isSingular()) {
			return ignored -> Shrinkable.unshrinkable(range.min);
		}
		return partitionedGenerator(range, partitionPoints);
	}

	private static RandomGenerator<BigInteger> partitionedGenerator(
		Range<BigInteger> range, BigInteger[] partitionPoints
	) {
		List<RandomGenerator<BigInteger>> generators = createPartitions(range, partitionPoints);
		if (generators.size() == 1) {
			return generators.get(0);
		}
		return random -> generators.get(random.nextInt(generators.size())).next(random);
	}

	private static List<RandomGenerator<BigInteger>> createPartitions(
		Range<BigInteger> range, BigInteger[] partitionPoints
	) {
		List<RandomGenerator<BigInteger>> partitions = new ArrayList<>();
		Arrays.sort(partitionPoints);
		BigInteger lower = range.min;
		for (BigInteger partitionPoint : partitionPoints) {
			BigInteger upper = partitionPoint;
			if (upper.compareTo(lower) <= 0) {
				continue;
			}
			if (upper.compareTo(range.max) >= 0) {
				break;
			}
			partitions.add(createBaseGenerator(lower, upper.subtract(BigInteger.ONE), range));
			lower = upper;
		}
		partitions.add(createBaseGenerator(lower, range.max, range));
		return partitions;
	}

	private static RandomGenerator<BigInteger> createBaseGenerator(BigInteger minGenerate, BigInteger maxGenerate, Range<BigInteger> shrinkingRange) {
		if (isWithinIntegerRange(minGenerate, maxGenerate)) {
			return createIntegerGenerator(minGenerate, maxGenerate, shrinkingRange);
		} else {
			return createBigIntegerGenerator(minGenerate, maxGenerate, shrinkingRange);
		}
	}

	private static RandomGenerator<BigInteger> createBigIntegerGenerator(BigInteger minGenerate, BigInteger maxGenerate, Range<BigInteger> shrinkingRange) {
		BigInteger range = maxGenerate.subtract(minGenerate);
		int bits = range.bitLength();
		return random -> {
			while (true) {
				BigInteger rawValue = new BigInteger(bits, random);
				BigInteger value = rawValue.add(minGenerate);
				if (value.compareTo(minGenerate) >= 0 && value.compareTo(maxGenerate) <= 0) {
					return new ShrinkableBigInteger(value, shrinkingRange);
				}
			}
		};
	}

	private static RandomGenerator<BigInteger> createIntegerGenerator(BigInteger min, BigInteger max, Range<BigInteger> shrinkingRange) {
		final int _min = Math.min(min.intValue(), max.intValue());
		final int _max = Math.max(min.intValue(), max.intValue());
		return random -> {
			int bound = Math.abs(_max - _min) + 1;
			int value = random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + _min;
			return new ShrinkableBigInteger(BigInteger.valueOf(value), shrinkingRange);
		};
	}

	private static boolean isWithinIntegerRange(BigInteger min, BigInteger max) {
		return min.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0
			&& max.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0;
	}

	// TODO: This could be way more sophisticated
	public static BigInteger[] calculateDefaultPartitionPoints(int tries, BigInteger min, BigInteger max) {
		int partitionPoint = Math.max(tries / 2, 10);
		BigInteger upperPartitionPoint = BigInteger.valueOf(partitionPoint).min(max);
		BigInteger lowerPartitionPoint = BigInteger.valueOf(partitionPoint).negate().max(min);
		return new BigInteger[]{lowerPartitionPoint, upperPartitionPoint};
	}
}
