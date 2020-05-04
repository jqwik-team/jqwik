package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;

public class InjectDuplicatesGenerator<T> implements RandomGenerator<T> {

	private final List<Long> previousSeeds = new ArrayList<>();
	private final RandomGenerator<T> base;
	private final double duplicateProbability;

	public InjectDuplicatesGenerator(RandomGenerator<T> base, double duplicateProbability) {this.base = base;
		this.duplicateProbability = duplicateProbability;
	}

	@Override
	public Shrinkable<T> next(Random random) {
		long seed = chooseSeed(random);
		return base.next(SourceOfRandomness.newRandom(seed));
	}

	long chooseSeed(Random random) {
		if (!previousSeeds.isEmpty()) {
			if (random.nextDouble() <= duplicateProbability) {
				return randomPreviousSeed(random);
			}
		}
		long seed = random.nextLong();
		previousSeeds.add(seed);
		return seed;
	}

	private long randomPreviousSeed(Random random) {
		int index = random.nextInt(previousSeeds.size());
		return previousSeeds.get(index);
	}
}
