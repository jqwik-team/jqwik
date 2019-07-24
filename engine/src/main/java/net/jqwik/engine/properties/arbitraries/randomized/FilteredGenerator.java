package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class FilteredGenerator<T> implements RandomGenerator<T> {
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
		Shrinkable<T> accepted = MaxTriesLoop.loop(
			() -> true,
			next -> {
				next = fetchShrinkable.apply(random);
				if (filterPredicate.test(next.value())) {
					return Tuple.of(true, next);
				}
				return Tuple.of(false, next);
			},
			(maxMisses) -> {
				String message = String.format("%s missed more than %s times.", toString(), maxMisses);
				return new TooManyFilterMissesException(message);
			}
		);
		return new FilteredShrinkable<>(accepted, filterPredicate);
	}

}
