package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.RandomDistribution.*;
import net.jqwik.engine.properties.*;

class BiasedNumericGenerator implements RandomNumericGenerator {

	private final RandomNumericGenerator partitionedGenerator;

	BiasedNumericGenerator(int genSize, BigInteger min, BigInteger max, BigInteger center) {
		List<BigInteger> partitionPoints = BiasedPartitionPointsCalculator.calculatePartitionPoints(genSize, min, max, center);
		Range<BigInteger> range = Range.of(min, max);
		partitionedGenerator = partitionedGenerator(range, partitionPoints);
	}

	@Override
	public BigInteger next(JqwikRandom random) {
		return partitionedGenerator.next(random);
	}

	private RandomNumericGenerator partitionedGenerator(
		Range<BigInteger> range,
		List<BigInteger> partitionPoints
	) {
		if (partitionPoints.isEmpty()) {
			return createUniformGenerator(range.min, range.max);
		}
		List<RandomNumericGenerator> generators = createPartitions(range, partitionPoints);
		return random -> generators.get(random.nextInt(generators.size())).next(random);
	}

	private List<RandomNumericGenerator> createPartitions(
		Range<BigInteger> range,
		List<BigInteger> partitionPoints
	) {
		List<RandomNumericGenerator> partitions = new ArrayList<>();
		Collections.sort(partitionPoints);
		BigInteger lower = range.min;
		for (BigInteger partitionPoint : partitionPoints) {
			BigInteger upper = partitionPoint;
			if (upper.compareTo(lower) <= 0) {
				continue;
			}
			if (upper.compareTo(range.max) >= 0) {
				break;
			}
			partitions.add(createUniformGenerator(lower, upper.subtract(BigInteger.ONE)));
			lower = upper;
		}
		partitions.add(createUniformGenerator(lower, range.max));
		return partitions;
	}

	private RandomNumericGenerator createUniformGenerator(
		BigInteger minGenerate,
		BigInteger maxGenerate
	) {
		int ignoredGenSize = 1000;
		BigInteger ignoredCenter = minGenerate;
		return RandomDistribution.uniform().createGenerator(ignoredGenSize, minGenerate, maxGenerate, ignoredCenter);
	}

}
