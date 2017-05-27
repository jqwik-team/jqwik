package net.jqwik.properties;

import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.concurrent.atomic.*;

public class ArbitraryWheelForTests<T> implements Arbitrary<T> {
	private final T[] values;

	public ArbitraryWheelForTests(T... values) {
		this.values = values;
	}

	@Override
	public RandomGenerator<T> generator(int tries) {
		AtomicInteger index = new AtomicInteger(0);
		return new RandomGenerator<T>() {
			@Override
			public Shrinkable<T> next(Random random) {
				if (index.get() < values.length) {
					int current = index.getAndIncrement();
					T value = values[current];
					ShrinkCandidates<T> shrinker = new WheelShrinker();
					return new ShrinkableValue<>(value, shrinker);
				} else {
					index.set(0);
					return next(random);
				}
			}
		};
	}

	private class WheelShrinker implements ShrinkCandidates<T> {

		@Override
		public Set<T> nextCandidates(T value) {
			int index = Arrays.asList(values).indexOf(value);
			if (index <= 0)
				return Collections.emptySet();
			T shrunkValue = values[index - 1];
			return Collections.singleton(shrunkValue);
		}

		@Override
		public int distance(T value) {
			return Math.max(Arrays.asList(values).indexOf(value), 0);
		}
	}
}
