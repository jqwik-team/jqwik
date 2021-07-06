package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

class SizeGenerator {

	private SizeGenerator() {
	}

	static Function<Random, Integer> create(int minSize, int maxSize, int genSize, RandomDistribution distribution) {
		if (distribution != null) {
			return sizeGeneratorWithDistribution(minSize, maxSize, genSize, distribution);
		}
		return sizeGeneratorWithCutoff(minSize, maxSize, genSize);
	}

	private static Function<Random, Integer> sizeGeneratorWithDistribution(
		int minSize,
		int maxSize,
		int genSize,
		RandomDistribution distribution
	) {
		RandomDistribution.RandomNumericGenerator generator =
			distribution.createGenerator(
				genSize,
				BigInteger.valueOf(minSize), BigInteger.valueOf(maxSize),
				BigInteger.valueOf(minSize)
			);
		return random -> generator.next(random).intValueExact();
	}

	private static Function<Random, Integer> sizeGeneratorWithCutoff(int minSize, int maxSize, int genSize) {
		int cutoffSize = cutoffSize(minSize, maxSize, genSize);
		if (cutoffSize >= maxSize)
			return random -> randomSize(random, minSize, maxSize);
		// Choose size below cutoffSize with probability of 0.9
		return random -> {
			if (random.nextDouble() > 0.1)
				return randomSize(random, minSize, cutoffSize);
			else
				return randomSize(random, cutoffSize + 1, maxSize);
		};
	}

	private static int cutoffSize(int minSize, int maxSize, int genSize) {
		int range = maxSize - minSize;
		int offset = (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		if (range <= offset)
			return maxSize;
		return Math.min(offset + minSize, maxSize);
	}

	private static int randomSize(Random random, int minSize, int maxSize) {
		int range = maxSize - minSize;
		return random.nextInt(range + 1) + minSize;
	}

}
