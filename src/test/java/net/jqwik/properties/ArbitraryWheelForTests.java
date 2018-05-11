package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import net.jqwik.support.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class ArbitraryWheelForTests<T> implements Arbitrary<T> {
	private final T[] values;

	@SafeVarargs
	public ArbitraryWheelForTests(T... values) {
		this.values = values;
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		AtomicInteger index = new AtomicInteger(0);
		return new RandomGenerator<T>() {
			@Override
			public Shrinkable<T> next(Random random) {
				if (index.get() < values.length) {
					int current = index.getAndIncrement();
					T value = values[current];
					return new WheelShrinkable(value);
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

	private class WheelShrinkable extends AbstractShrinkable<T> {

		WheelShrinkable(T value) {
			super(value);
		}

		private Set<T> nextCandidates(T value) {
			int index = Arrays.asList(values).indexOf(value);
			if (index <= 0)
				return Collections.emptySet();
			T shrunkValue = values[index - 1];
			return Collections.singleton(shrunkValue);
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(Math.max(Arrays.asList(values).indexOf(value()), 0));
		}

		@Override
		public Set<Shrinkable<T>> shrinkCandidatesFor(Shrinkable<T> shrinkable) {
			return nextCandidates(shrinkable.value()) //
													  .stream() //
													  .map(WheelShrinkable::new) //
													  .collect(Collectors.toSet());
		}

	}
}
