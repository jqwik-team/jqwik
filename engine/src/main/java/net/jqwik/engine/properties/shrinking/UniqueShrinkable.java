package net.jqwik.engine.properties.shrinking;

import java.util.*;
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
	public Stream<Shrinkable<T>> shrink() {
		return toFilter.shrink().filter(s -> {
			return !usedValues.contains(s.value());
		}).map(s -> {
			// TODO: In theory the set of used values should only contain those in the current try
			// but currently it contains all values tried in this shrinking
			usedValues.add(s.value());
			return new UniqueShrinkable<>(s, usedValues);
		});
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

}
