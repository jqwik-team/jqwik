package net.jqwik.engine.properties.shrinking;

import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

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
		return JqwikStreamSupport.concat(
			shrinkToFirst(),
			deepSearchFirst()
		);
	}

	private Stream<Shrinkable<T>> shrinkToFirst() {
		return toFilter.shrink()
					   .filter(this::isIncluded)
					   .map(this::toFiltered);
	}

	private Stream<Shrinkable<T>> deepSearchFirst() {
		return toFilter.shrink()
					   .flatMap(Shrinkable::shrink)
					   .flatMap(shrinkable -> {
						   if (isIncluded(shrinkable)) {
							   return Stream.of(shrinkable);
						   } else {
							   // Is the limit necessary?
							   return shrinkable.shrink()
												.limit(100);
						   }
					   })
					   .findFirst()
					   .map(t -> Stream.of(toFiltered(t)))
					   .orElse(Stream.empty());
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
