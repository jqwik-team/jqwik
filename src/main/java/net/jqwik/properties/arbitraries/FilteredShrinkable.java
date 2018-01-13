package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class FilteredShrinkable<T> implements Shrinkable<T> {
	private final Shrinkable<T> toFilter;
	private final Predicate<T> filterPredicate;

	public FilteredShrinkable(Shrinkable<T> toFilter, Predicate<T> filterPredicate) {
		this.toFilter = toFilter;
		this.filterPredicate = filterPredicate;
	}

	@Override
	public Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		Set<ShrinkResult<Shrinkable<T>>> branches = toFilter.shrinkNext(falsifier) //
															.stream() //
															.map(shrinkResult -> shrinkResult //
						.map(shrinkable -> (Shrinkable<T>) new FilteredShrinkable<>(shrinkable, filterPredicate))) //
															.collect(Collectors.toSet());
		return firstFalsifiedFitPerBranch(branches, falsifier);
	}

	private Set<ShrinkResult<Shrinkable<T>>> firstFalsifiedFitPerBranch(Set<ShrinkResult<Shrinkable<T>>> branches,
																		Predicate<T> falsifier) {
		Set<ShrinkResult<Shrinkable<T>>> fits = new HashSet<>();
		for (ShrinkResult<Shrinkable<T>> branch : branches) {
			if (filterPredicate.test(branch.shrunkValue().value()))
				fits.add(branch);
			else {
				Set<ShrinkResult<Shrinkable<T>>> newBranches = branch.shrunkValue().shrinkNext(falsifier);
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

	@Override
	public String toString() {
		return String.format("FilteredShrinkable[%s:%d]", value(), distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Shrinkable))
			return false;
		Shrinkable<?> that = (Shrinkable<?>) o;
		return Objects.equals(value(), that.value());
	}

	@Override
	public int hashCode() {
		return toFilter.hashCode();
	}
}
