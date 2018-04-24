package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;
import org.opentest4j.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class FilteredShrinkable<T> implements Shrinkable<T> {
	private final Shrinkable<T> toFilter;
	private final Predicate<T> filterPredicate;

	public FilteredShrinkable(Shrinkable<T> toFilter, Predicate<T> filterPredicate) {
		this.toFilter = toFilter;
		this.filterPredicate = filterPredicate;
	}

	@Override
	public Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		Set<ShrinkResult<Shrinkable<T>>> candidates = toFilter.shrinkNext(falsifier);
		Set<ShrinkResult<Shrinkable<T>>> branches =
			candidates.stream() //
					  .map(shrinkResult -> shrinkResult.map(this::asFilteredShrinkable)) //
					  .collect(Collectors.toSet());
		return minDistanceFalsifiedFitsPerBranch(branches, falsifier);
	}

	private Shrinkable<T> asFilteredShrinkable(Shrinkable<T> shrinkable) {
		return new FilteredShrinkable<>(shrinkable, filterPredicate);
	}

	private Set<ShrinkResult<Shrinkable<T>>> minDistanceFalsifiedFitsPerBranch(
		Set<ShrinkResult<Shrinkable<T>>> branches,
		Predicate<T> falsifier
	) {
		Set<ShrinkResult<Shrinkable<T>>> fits = new HashSet<>();
		Set<ShrinkResult<Shrinkable<T>>> nonFits = new HashSet<>();
		for (ShrinkResult<Shrinkable<T>> branch : branches) {
			if (branchFits(branch))
				fits.add(branch);
			else {
				nonFits.add(branch);
			}
		}
		ShrinkingHelper
			.minDistanceStream(nonFits)
			.forEach(nonFit -> {
				Set<ShrinkResult<Shrinkable<T>>> newBranches = nonFit.shrunkValue().shrinkNext(falsifier);
				ShrinkingHelper
					.minDistanceStream(newBranches)
					.forEach(fits::add);
			});

		return fits;
	}

	private boolean branchFits(ShrinkResult<Shrinkable<T>> branch) {
		if (branch.throwable().isPresent() && branch.throwable().get() instanceof TestAbortedException)
			return false;
		return filterPredicate.test(branch.shrunkValue().value());
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
