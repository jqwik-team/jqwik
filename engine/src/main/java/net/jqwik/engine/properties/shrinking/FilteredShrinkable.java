package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class FilteredShrinkable<T> implements Shrinkable<T> {

	private final Shrinkable<T> toFilter;
	private final Predicate<T> filter;

	public FilteredShrinkable(Shrinkable<T> toFilter, Predicate<T> filter) {
		this.toFilter = toFilter;
		this.filter = filter;
	}

	@Override
	public T value() {
		return toFilter.value();
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return Stream.concat(
			shrinkToFirst(toFilter),
			deepSearchFirst(toFilter)
		);
	}

	@Override
	public Optional<Shrinkable<T>> grow(Shrinkable<?> before, Shrinkable<?> after) {
		if (before instanceof FilteredShrinkable && after instanceof FilteredShrinkable) {
			Shrinkable<?> beforeToFilter = ((FilteredShrinkable<?>) before).toFilter;
			Shrinkable<?> afterToFilter = ((FilteredShrinkable<?>) after).toFilter;
			return toFilter.grow(beforeToFilter, afterToFilter)
						   .filter(this::isIncluded)
						   .map(this::toFiltered);
		}
		return toFilter.grow(before, after)
					   .filter(this::isIncluded)
					   .map(this::toFiltered);
	}

	private Stream<Shrinkable<T>> shrinkToFirst(Shrinkable<T> base) {
		return base.shrink()
				   .filter(this::isIncluded)
				   .map(this::toFiltered);
	}

	private Stream<Shrinkable<T>> deepSearchFirst(Shrinkable<T> base) {
		return Stream.concat(
			base.shrink().flatMap(this::shrinkToFirst),
			base.shrink().flatMap(this::deepSearchFirst).limit(1)
		);
	}

	private boolean isIncluded(Shrinkable<T> shrinkable) {
		return filter.test(shrinkable.value());
	}

	private Shrinkable<T> toFiltered(Shrinkable<T> t) {
		return new FilteredShrinkable<>(t, filter);
	}

	@Override
	public ShrinkingDistance distance() {
		return toFilter.distance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FilteredShrinkable<?> that = (FilteredShrinkable<?>) o;
		return toFilter.equals(that.toFilter);
	}

	@Override
	public int hashCode() {
		return toFilter.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Filtered|%s", toFilter);
	}

}
