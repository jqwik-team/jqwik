package net.jqwik.properties.newShrinking;

import java.util.function.*;

public class NFilteredShrinkable<T> implements NShrinkable<T> {

	private final NShrinkable<T> toFilter;
	private final Predicate<T> filter;

	public NFilteredShrinkable(NShrinkable<T> toFilter, Predicate<T> filter) {
		this.toFilter = toFilter;
		this.filter = filter;
	}

	@Override
	public T value() {
		return toFilter.value();
	}

	@Override
	public ShrinkingSequence<T> shrink(Falsifier<T> falsifier) {
		return new FilteredShrinkingSequence(falsifier);
	}

	@Override
	public ShrinkingDistance distance() {
		return toFilter.distance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NFilteredShrinkable<?> that = (NFilteredShrinkable<?>) o;
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

	private class FilteredShrinkingSequence implements ShrinkingSequence<T> {

		private final ShrinkingSequence<T> toFilterSequence;

		private FilteredShrinkingSequence(Falsifier<T> falsifier) {
			Falsifier<T> filteredFalsifier = falsifier.withFilter(filter);
			toFilterSequence = toFilter.shrink(filteredFalsifier);
		}

		@Override
		public boolean next(Runnable count, Consumer<T> reportFalsified) {
			return toFilterSequence.next(count, reportFalsified);
		}

		@Override
		public NShrinkable<T> current() {
			return toFilterSequence.current().filter(filter);
		}
	}
}
