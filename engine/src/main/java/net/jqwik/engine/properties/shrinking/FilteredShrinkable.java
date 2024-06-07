package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;

public class FilteredShrinkable<T extends @Nullable Object> implements Shrinkable<T> {

	private static final int MAX_BASE_SHRINKS = 100;
	private final AtomicInteger countBaseShrinks = new AtomicInteger(0);

	private final Shrinkable<T> toFilter;
	private final Predicate<? super T> filter;

	public FilteredShrinkable(Shrinkable<T> toFilter, Predicate<? super T> filter) {
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
			shrinkDeep(toFilter)
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
				   .peek(ignore -> countBaseShrinks.incrementAndGet())
				   .filter(this::isIncluded)
				   .map(this::toFiltered);
	}

	private Stream<Shrinkable<T>> shrinkDeep(Shrinkable<T> base) {
		// This is a terrible hack I will have to suffer for one day
		Stream<Shrinkable<T>> constrainedBaseShrink =
			JqwikStreamSupport.takeWhile(
				base.shrink(),
				ignore -> countBaseShrinks.get() < MAX_BASE_SHRINKS
			);
		return JqwikStreamSupport.concat(
			base.shrink().flatMap(this::shrinkToFirst),
			constrainedBaseShrink.flatMap(this::shrinkDeep).limit(1)
		);
	}

	@Override
	public Stream<Shrinkable<T>> grow() {
		return Stream.concat(
			growToFirst(toFilter),
			growDeep(toFilter)
		).limit(50).distinct();
	}

	private Stream<Shrinkable<T>> growToFirst(Shrinkable<T> base) {
		return base.grow()
				   .filter(this::isIncluded)
				   .map(this::toFiltered);
	}

	private Stream<Shrinkable<T>> growDeep(Shrinkable<T> base) {
		return Stream.concat(
			base.grow().flatMap(this::growToFirst),
			base.grow().flatMap(this::growDeep)
		).limit(10); // Might be too few for some shrinking problems
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
