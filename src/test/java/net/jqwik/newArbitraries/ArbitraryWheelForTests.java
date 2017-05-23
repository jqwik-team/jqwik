package net.jqwik.newArbitraries;

import java.util.*;
import java.util.concurrent.atomic.*;

public class ArbitraryWheelForTests<T> implements NArbitrary<T> {
	private final T[] values;

	public ArbitraryWheelForTests(T... values) {
		this.values = values;
	}

	@Override
	public NShrinkableGenerator<T> generator(int tries) {
		AtomicInteger index = new AtomicInteger(0);
		return new NShrinkableGenerator<T>() {
			@Override
			public NShrinkable<T> next(Random random) {
				if (index.get() < values.length) {
					int current = index.getAndIncrement();
					T value = values[current];
					NShrinker<T> shrinker = new WheelShrinker();
					return new NShrinkableValue<>(value, shrinker);
				} else {
					index.set(0);
					return next(random);
				}
			}
		};
	}

	private class WheelShrinker implements NShrinker<T> {

		@Override
		public Set<T> nextShrinkingCandidates(T value) {
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
