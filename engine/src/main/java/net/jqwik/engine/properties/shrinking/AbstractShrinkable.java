package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

public abstract class AbstractShrinkable<T> implements Shrinkable<T> {

	private final T value;

	public AbstractShrinkable(T value) {
		this.value = value;
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return shrinkCandidatesFor(this).stream().sorted(Comparator.comparing(Shrinkable::distance));
	}

	public abstract Set<Shrinkable<T>> shrinkCandidatesFor(Shrinkable<T> shrinkable);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractShrinkable<?> that = (AbstractShrinkable<?>) o;

		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return String.format("%s<%s>(%s:%s)", //
							 getClass().getSimpleName(), //
							 value().getClass().getSimpleName(), //
							 value(), distance());
	}
}
