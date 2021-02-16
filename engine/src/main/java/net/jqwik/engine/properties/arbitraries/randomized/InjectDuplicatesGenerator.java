package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.lifecycle.*;

public class InjectDuplicatesGenerator<T> implements RandomGenerator<T> {

	private final Store<List<Long>> previousSeeds = createStore(this, Lifespan.TRY, ArrayList::new);

	// TODO: This should be some generic functionality for stores possibly used outside jqwik's lifecycle
	private static <T> Store<T> createStore(Object identifier, Lifespan lifespan, Supplier<T> initializer) {
		try {
			return Store.create(identifier, lifespan, initializer);
		} catch (OutsideJqwikException jqwikException) {
			return new Store<T>() {
				T t = initializer.get();

				@Override
				public T get() {
					return t;
				}

				@Override
				public Lifespan lifespan() {
					return lifespan;
				}

				@Override
				public void update(Function<T, T> updater) {
					t = updater.apply(t);
				}

				@Override
				public void reset() {
					t = initializer.get();
				}

				@Override
				public Store<T> onClose(Consumer<T> onCloseCallback) {
					return this;
				}
			};
		}
	}

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
