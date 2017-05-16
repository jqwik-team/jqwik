package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NFilteredShrinkable<T> implements NShrinkable<T> {
	private final NShrinkable<T> toFilter;
	private final Predicate<T> filterPredicate;

	public NFilteredShrinkable(NShrinkable<T> toFilter, Predicate<T> filterPredicate) {
		this.toFilter = toFilter;
		this.filterPredicate = filterPredicate;
	}

	@Override
	public Set<NShrinkable<T>> shrink() {
		return firstFitPerBranch(toFilter.shrink(), filterPredicate);
	}

	@Override
	public boolean falsifies(Predicate<T> falsifier) {
		return toFilter.falsifies(falsifier);
	}

	@Override
	public T value() {
		return toFilter.value();
	}

	@Override
	public int distance() {
		return toFilter.distance();
	}

	private Set<NShrinkable<T>> firstFitPerBranch(Set<NShrinkable<T>> branches, Predicate<T> filterPredicate) {
		Set<NShrinkable<T>> fits = new HashSet<>();
		for (NShrinkable<T> branch : branches) {
			if (filterPredicate.test(branch.value()))
				fits.add(branch);
			else
				fits.addAll(firstFitPerBranch(branch.shrink(), filterPredicate));
		}
		return fits;
	}

}
