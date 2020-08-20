package net.jqwik.engine.properties.shrinking;

import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class UniqueShrinkable<T> implements Shrinkable<T> {

	public final Shrinkable<T> toFilter;
	private final Function<UniqueShrinkable<T>, Stream<Shrinkable<T>>> shrinker;

	public UniqueShrinkable(Shrinkable<T> toFilter, Function<UniqueShrinkable<T>, Stream<Shrinkable<T>>> shrinker) {
		this.toFilter = toFilter;
		this.shrinker = shrinker;
	}

	@Override
	public T value() {
		return toFilter.value();
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return shrinker.apply(this);
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
