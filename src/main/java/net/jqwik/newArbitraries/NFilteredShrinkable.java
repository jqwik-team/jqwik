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
	public Set<NShrinkResult<NShrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		Set<NShrinkResult<NShrinkable<T>>> branches = toFilter.shrinkNext(falsifier);
		return firstFalsifiedFitPerBranch(branches, falsifier);
	}

	private Set<NShrinkResult<NShrinkable<T>>> firstFalsifiedFitPerBranch(Set<NShrinkResult<NShrinkable<T>>> branches,
			Predicate<T> falsifier) {
		Set<NShrinkResult<NShrinkable<T>>> fits = new HashSet<>();
		for (NShrinkResult<NShrinkable<T>> branch : branches) {
			if (filterPredicate.test(branch.shrunkValue().value()))
				fits.add(branch);
			else {
				Set<NShrinkResult<NShrinkable<T>>> newBranches = branch.shrunkValue().shrinkNext(falsifier);
				fits.addAll(firstFalsifiedFitPerBranch(newBranches, falsifier));
			}
		}
		return fits;
	}

	@Override
	public T value() {
		return toFilter.value();
	}

	@Override
	public int distance() {
		return toFilter.distance();
	}

}
