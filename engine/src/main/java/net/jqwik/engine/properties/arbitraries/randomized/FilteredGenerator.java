package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class FilteredGenerator<T> implements RandomGenerator<T> {
	private final RandomGenerator<T> toFilter;
	private final Predicate<T> filterPredicate;
	private int maxMisses;

	public FilteredGenerator(RandomGenerator<T> toFilter, Predicate<T> filterPredicate, int maxMisses) {
		this.toFilter = toFilter;
		this.filterPredicate = filterPredicate;
		this.maxMisses = maxMisses;
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
		Shrinkable<T> accepted = MaxTriesLoop.loop(
			() -> true,
			next -> {
				next = fetchShrinkable.apply(random);
				if (filterPredicate.test(next.value())) {
					return Tuple.of(true, next);
				}
				return Tuple.of(false, next);
			},
			(missed) -> {
				String message = String.format("%s missed more than %s times.", toString(), missed);
				return new TooManyFilterMissesException(message);
			},
			maxMisses
		);
		return new FilteredShrinkable<>(accepted, filterPredicate);
	}

}
