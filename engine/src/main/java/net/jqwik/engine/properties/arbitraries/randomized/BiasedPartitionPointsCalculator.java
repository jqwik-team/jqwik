package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;

class BiasedPartitionPointsCalculator {

	public static List<BigInteger> calculatePartitionPoints(
		int genSize, // currently ignored but that may change one day
		BigInteger min,
		BigInteger max,
		BigInteger shrinkingTarget
	) {
		List<BigInteger> partitions = new ArrayList<>();
		if (shrinkingTarget.compareTo(max) >= 0) {
			partitions.addAll(partitions(min, max, BigInteger.ONE.negate()));
		} else if (shrinkingTarget.compareTo(min) <= 0) {
			partitions.addAll(partitions(max, min, BigInteger.ONE));
		} else {
			partitions.addAll(partitions(min, shrinkingTarget, BigInteger.ONE.negate()));
			partitions.addAll(partitions(max, shrinkingTarget, BigInteger.ONE));
			if (partitions.size() > 0) {
				partitions.add(shrinkingTarget);
			}
		}
		return partitions;
	}

	private static List<BigInteger> partitions(BigInteger from, BigInteger to, BigInteger step) {
		List<BigInteger> partitions = new ArrayList<>();
		BigInteger partitionPoint = from;
		while (true) {
			BigInteger range = to.subtract(partitionPoint).abs();
			if (range.compareTo(BigInteger.valueOf(20)) <= 0) {
				return partitions;
			}
			BigInteger partitionRatio = partitionRatio(range);
			BigInteger distance = range.divide(partitionRatio);
			BigInteger nextPartitionPoint = to.add(step.multiply(distance));
			if (distance.compareTo(BigInteger.ZERO) == 0) {
				break;
			}
			partitions.add(nextPartitionPoint);
			partitionPoint = nextPartitionPoint;
		}
		return partitions;
	}

	private static BigInteger partitionRatio(BigInteger range) {
		int approximatedDecimals = range.bitLength() / 10 * 3;
		long ratio = Math.max((long) Math.pow(approximatedDecimals / 5.0, 10), 3);
		return BigInteger.valueOf(ratio);
	}

}
