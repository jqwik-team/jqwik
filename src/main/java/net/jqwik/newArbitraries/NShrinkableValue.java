package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NShrinkableValue<T> implements NShrinkable<T> {

	private final T value;
	private final int distance;
	private final NShrinker<T> shrinker;

	public NShrinkableValue(T value, int distance, NShrinker<T> shrinker) {
		this.value = value;
		this.distance = distance;
		this.shrinker = shrinker;
	}

	@Override
	public Set<NShrinkable<T>> shrink() {
		return shrinker.shrink();
	}

	@Override
	public boolean falsifies(Predicate<T> falsifier) {
		return falsifier.negate().test(value);
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public int distance() {
		return distance;
	}

	@Override
	public String toString() {
		return String.format("ShrinkableValue[%s:%d]", value(), distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NShrinkableValue<?> that = (NShrinkableValue<?>) o;
		return distance == that.distance &&
			Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, distance);
	}
}
