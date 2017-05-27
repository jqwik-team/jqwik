package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.*;

public class NFilteredGenerator<T> implements NShrinkableGenerator<T> {
	private final NShrinkableGenerator<T> toFilter;
	private final Predicate<T> filterPredicate;

	public NFilteredGenerator(NShrinkableGenerator<T> toFilter, Predicate<T> filterPredicate) {
		this.toFilter = toFilter;
		this.filterPredicate = filterPredicate;
	}

	@Override
	public NShrinkable<T> next(Random random) {
		while (true) {
			NShrinkable<T> next = toFilter.next(random);
			if (filterPredicate.test(next.value()))
				return new NFilteredShrinkable<>(next, filterPredicate);
		}
	}
}
