package net.jqwik.properties;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.support.*;

public class ArbitraryWheelForTests<T> implements Arbitrary<T> {
	private final T[] values;

	@SafeVarargs
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

			@Override
			public String toString() {
				return String.format("Wheel: %s", JqwikStringSupport.displayString(values));
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
