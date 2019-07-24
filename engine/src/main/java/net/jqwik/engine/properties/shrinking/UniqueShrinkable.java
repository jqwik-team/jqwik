package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class UniqueShrinkable<T> implements Shrinkable<T> {

	private final Shrinkable<T> toFilter;
	private final Set<T> usedValues;
	private final T value;

	public UniqueShrinkable(Shrinkable<T> toFilter, Set<T> usedValues) {
		this.toFilter = toFilter;
		this.usedValues = usedValues;
		this.value = toFilter.value();
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public ShrinkingSequence<T> shrink(Falsifier<T> falsifier) {
		return new UniqueShrinkingSequence(falsifier);
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
