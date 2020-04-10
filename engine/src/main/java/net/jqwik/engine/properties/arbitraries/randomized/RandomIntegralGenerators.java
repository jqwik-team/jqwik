package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

// TODO: Remove duplication with RandomDecimalGenerators
public class RandomIntegralGenerators {

	public static RandomGenerator<BigInteger> bigIntegers(
		Range<BigInteger> range,
		BigInteger[] partitionPoints,
		Function<BigInteger, BigInteger> shrinkingTargetCalculator
	) {
		if (range.isSingular()) {
			return ignored -> Shrinkable.unshrinkable(range.min);
		}
		return partitionedGenerator(range, partitionPoints, shrinkingTargetCalculator);
	}

	private static RandomGenerator<BigInteger> partitionedGenerator(
		Range<BigInteger> range,
		BigInteger[] partitionPoints,
		Function<BigInteger, BigInteger> shrinkingTargetCalculator
	) {
		List<RandomGenerator<BigInteger>> generators = createPartitions(range, partitionPoints, shrinkingTargetCalculator);
		if (generators.size() == 1) {
			return generators.get(0);
		}
		return random -> generators.get(random.nextInt(generators.size())).next(random);
	}

	private static List<RandomGenerator<BigInteger>> createPartitions(
		Range<BigInteger> range, BigInteger[] partitionPoints,
		Function<BigInteger, BigInteger> shrinkingTargetCalculator
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
			partitions.add(createBaseGenerator(lower, upper.subtract(BigInteger.ONE), range, shrinkingTargetCalculator));
			lower = upper;
		}
		partitions.add(createBaseGenerator(lower, range.max, range, shrinkingTargetCalculator));
		return partitions;
	}

	private static RandomGenerator<BigInteger> createBaseGenerator(
		BigInteger minGenerate,
		BigInteger maxGenerate,
		Range<BigInteger> shrinkingRange,
		Function<BigInteger, BigInteger> shrinkingTargetCalculator
	) {
		if (isWithinIntegerRange(minGenerate, maxGenerate)) {
			return createIntegerGenerator(minGenerate, maxGenerate, shrinkingRange, shrinkingTargetCalculator);
		} else {
			return createBigIntegerGenerator(minGenerate, maxGenerate, shrinkingRange, shrinkingTargetCalculator);
		}
	}

	private static RandomGenerator<BigInteger> createBigIntegerGenerator(
		BigInteger minGenerate,
		BigInteger maxGenerate,
		Range<BigInteger> shrinkingRange,
		Function<BigInteger, BigInteger> shrinkingTargetCalculator
	) {
		BigInteger range = maxGenerate.subtract(minGenerate);
		int bits = range.bitLength();
		return random -> {
			while (true) {
				BigInteger rawValue = new BigInteger(bits, random);
				BigInteger value = rawValue.add(minGenerate);
				if (value.compareTo(minGenerate) >= 0 && value.compareTo(maxGenerate) <= 0) {
					return new ShrinkableBigInteger(
						value,
						shrinkingRange,
						shrinkingTargetCalculator.apply(value)
					);
				}
			}
		};
	}

	private static RandomGenerator<BigInteger> createIntegerGenerator(
		BigInteger min,
		BigInteger max,
		Range<BigInteger> shrinkingRange,
		Function<BigInteger, BigInteger> shrinkingTargetCalculator
	) {
		final int _min = Math.min(min.intValue(), max.intValue());
		final int _max = Math.max(min.intValue(), max.intValue());
		return random -> {
			int bound = Math.abs(_max - _min) + 1;
			int value = random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + _min;
			BigInteger bigIntegerValue = BigInteger.valueOf(value);
			return new ShrinkableBigInteger(
				bigIntegerValue,
				shrinkingRange,
				shrinkingTargetCalculator.apply(bigIntegerValue)
			);
		};
	}

	private static boolean isWithinIntegerRange(BigInteger min, BigInteger max) {
		return min.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0
				   && max.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0;
	}

}
