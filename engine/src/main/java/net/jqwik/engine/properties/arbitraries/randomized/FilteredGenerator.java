package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.JqwikRandom;
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
	public Shrinkable<T> next(JqwikRandom random) {
		return nextUntilAccepted(random, toFilter::next);
	}

	@Override
	public String toString() {
		return String.format("Filtering [%s]", toFilter);
	}

	private Shrinkable<T> nextUntilAccepted(JqwikRandom random, Function<JqwikRandom, Shrinkable<T>> fetchShrinkable) {
		for (int i = 0; i < maxMisses; i++) {
			Shrinkable<T> value = fetchShrinkable.apply(random);
			if (filterPredicate.test(value.value())) {
				return new FilteredShrinkable<>(value, filterPredicate);
			}
		}
		String message = String.format("%s missed more than %s times.", toString(), maxMisses);
		throw new TooManyFilterMissesException(message);
	}

}
