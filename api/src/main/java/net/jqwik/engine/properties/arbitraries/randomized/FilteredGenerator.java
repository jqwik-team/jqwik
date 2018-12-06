package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

public class FilteredGenerator<T> implements RandomGenerator<T> {
	private static final long MAX_MISSES = 10000;
	private final RandomGenerator<T> toFilter;
	private final Predicate<T> filterPredicate;

	public FilteredGenerator(RandomGenerator<T> toFilter, Predicate<T> filterPredicate) {
		this.toFilter = toFilter;
		this.filterPredicate = filterPredicate;
	}

	@Override
	public Shrinkable<T> next(Random random) {
		return nextUntilAccepted(random, toFilter::next);
	}

	@Override
	public String toString() {
		return String.format("Filtering [%s]", toFilter);
	}

	private Shrinkable<T> nextUntilAccepted(Random random, Function<Random, Shrinkable<T>> fetchShrinkable) {
		long count = 0;
		while (true) {
			Shrinkable<T> next = fetchShrinkable.apply(random);
			if (filterPredicate.test(next.value())) {
				return new FilteredShrinkable<>(next, filterPredicate);
			} else {
				if (++count > MAX_MISSES) {
					throw new TooManyFilterMissesException(String.format("%s missed more than %s times.", toString(), MAX_MISSES));
				}
			}

		}
	}

}
