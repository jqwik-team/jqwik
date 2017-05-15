package net.jqwik.newArbitraries;

import java.util.*;
import java.util.concurrent.atomic.*;

public class NArbitraryWheel<T> implements NArbitrary<T> {
	private final T[] values;

	public NArbitraryWheel(T... values) {
		this.values = values;
	}

	@Override
	public NShrinkableGenerator<T> generator(int tries) {
		AtomicInteger index = new AtomicInteger(0);
		return new NShrinkableGenerator<T>() {
			@Override
			public T next(Random random) {
				if (index.get() < values.length)
					return values[index.getAndIncrement()];
				else {
					index.set(0);
					return next(random);
				}
			}

			@Override
			public Set<NShrunkValue<T>> shrink(T valueToShrink) {
				int index = Arrays.asList(values).indexOf(valueToShrink);
				if (index <= 0)
					return Collections.emptySet();
				NShrunkValue<T> shrunkValue = new NShrunkValue<>(values[index - 1], index);
				return Collections.singleton(shrunkValue);
			}
		};
	}
}
