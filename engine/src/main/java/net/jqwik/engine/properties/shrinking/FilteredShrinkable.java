package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;

public class FilteredShrinkable<T> implements Shrinkable<T> {

	private final Shrinkable<T> toFilter;
	private final Predicate<T> filter;
	private final T value;

	public FilteredShrinkable(Shrinkable<T> toFilter, Predicate<T> filter) {
		this.toFilter = toFilter;
		this.filter = filter;
		this.value = toFilter.value();
	}

	@Override
	public T value() {
		return value;
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

	//TODO: Extract to ShrinkingSequence.filter() method
	private class FilteredShrinkingSequence implements ShrinkingSequence<T> {

		private final ShrinkingSequence<T> toFilterSequence;

		private FilteredShrinkingSequence(Falsifier<T> falsifier) {
			Falsifier<T> filteredFalsifier = falsifier.withFilter(filter);
			toFilterSequence = toFilter.shrink(filteredFalsifier);
		}

		@Override
		public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
			return toFilterSequence.next(count, falsifiedReporter);
		}

		@Override
		public FalsificationResult<T> current() {
			return toFilterSequence.current().filter(filter);
		}

		@Override
		public void init(FalsificationResult<T> initialCurrent) {
			toFilterSequence.init(initialCurrent);
		}
	}
}
