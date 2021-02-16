package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;

public class InjectDuplicatesGenerator<T> implements RandomGenerator<T> {

	private final Store<List<Long>> previousSeeds = Store.create(this, Lifespan.TRY, ArrayList::new);

	private final RandomGenerator<T> base;
	private final double duplicateProbability;

	public InjectDuplicatesGenerator(RandomGenerator<T> base, double duplicateProbability) {
		this.base = base;
		this.duplicateProbability = duplicateProbability;
	}

	@Override
	public Shrinkable<T> next(Random random) {
		long seed = chooseSeed(random);
		return base.next(SourceOfRandomness.newRandom(seed));
	}

	long chooseSeed(Random random) {
		if (!previousSeeds.get().isEmpty()) {
			if (random.nextDouble() <= duplicateProbability) {
				return randomPreviousSeed(random);
			}
		}
		long seed = random.nextLong();
		previousSeeds.get().add(seed);
		return seed;
	}

	private long randomPreviousSeed(Random random) {
		int index = random.nextInt(previousSeeds.get().size());
		return previousSeeds.get().get(index);
	}
}
