package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class UniqueShrinkable<T> implements Shrinkable<T> {

	private final Shrinkable<T> toFilter;
	private final Set<T> usedValues;

	// TODO: Collect set of used shrinkables instead of used values to prevent probs with mutable state
	public UniqueShrinkable(Shrinkable<T> toFilter, Set<T> usedValues) {
		this.toFilter = toFilter;
		this.usedValues = usedValues;
	}

	@Override
	public T value() {
		return toFilter.value();
	}

	@Override
	public T createValue() {
		return value();
	}

	@Override
	public ShrinkingSequence<T> shrink(Falsifier<T> falsifier) {
		return new UniqueShrinkingSequence(falsifier);
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return toFilter.shrink().filter(s -> !usedValues.contains(s.createValue()));
	}

	@Override
	public List<Shrinkable<T>> shrinkingSuggestions() {
		return toFilter.shrinkingSuggestions()
					   .stream()
					   .filter(shrinkable -> !usedValues.contains(shrinkable.value()))
					   .collect(Collectors.toList());
	}

	@Override
	public ShrinkingDistance distance() {
		return toFilter.distance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UniqueShrinkable<?> that = (UniqueShrinkable<?>) o;
		return toFilter.equals(that.toFilter);
	}

	@Override
	public int hashCode() {
		return toFilter.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Unique|%s", toFilter);
	}

	private class UniqueShrinkingSequence implements ShrinkingSequence<T> {

		private final ShrinkingSequence<T> uniqueSequence;

		private UniqueShrinkingSequence(Falsifier<T> falsifier) {
			Falsifier<T> uniqueFalsifier = falsifier.withFilter(value -> !usedValues.contains(value));
			uniqueSequence = toFilter.shrink(uniqueFalsifier);
		}

		@Override
		public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
			T valueToShrink = uniqueSequence.current().value();
			boolean hasNext = uniqueSequence.next(count, falsifiedReporter);
			if (hasNext) {
				usedValues.remove(valueToShrink);
				usedValues.add(uniqueSequence.current().value());
			}
			return hasNext;
		}

		@Override
		public void init(FalsificationResult<T> initialCurrent) {
			uniqueSequence.init(initialCurrent);
		}

		@Override
		public FalsificationResult<T> current() {
			return uniqueSequence.current().map(shrinkable -> new UniqueShrinkable<>(shrinkable, usedValues));
		}
	}
}
