package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;

public class InjectDuplicatesGenerator<T> implements RandomGenerator<T> {

	private final RandomGenerator<T> base;
	private final double duplicateProbability;
	private final Store<List<Long>> previousSeedsStore;

	public InjectDuplicatesGenerator(RandomGenerator<T> base, double duplicateProbability) {
		this.base = base;
		this.duplicateProbability = duplicateProbability;
		this.previousSeedsStore = createPreviousSeedsStorePerTry();
	}

	private Store<List<Long>> createPreviousSeedsStorePerTry() {
		return Store.getOrCreate(this, Lifespan.TRY, ArrayList::new);
	}

	@Override
	public Shrinkable<T> next(Random random) {
		long seed = chooseSeed(random);
		return base.next(SourceOfRandomness.newRandom(seed));
	}

	long chooseSeed(Random random) {
		List<Long> previousSeeds = previousSeedsStore.get();
		if (!previousSeeds.isEmpty()) {
			if (random.nextDouble() <= duplicateProbability) {
				return randomPreviousSeed(previousSeedsStore, random);
			}
		}
		long seed = random.nextLong();
		previousSeeds.add(seed);
		return seed;
	}

	private long randomPreviousSeed(Store<List<Long>> previousSeeds, Random random) {
		int index = random.nextInt(previousSeeds.get().size());
		return previousSeeds.get().get(index);
	}
}
