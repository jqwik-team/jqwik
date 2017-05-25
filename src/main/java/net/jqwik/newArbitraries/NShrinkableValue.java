package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NShrinkableValue<T> implements NShrinkable<T> {

	public static <T> NShrinkableValue<T> unshrinkable(T value) {
		return new NShrinkableValue<>(value, ignore -> Collections.emptySet());
	}

	private final T value;
	private final NShrinkCandidates<T> shrinker;

	public NShrinkableValue(T value, NShrinkCandidates<T> shrinker) {
		this.value = value;
		this.shrinker = shrinker;
	}

	@Override
	public Set<NShrinkResult<NShrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		return shrinker.nextCandidates(value).stream() //
				.map(shrunkValue -> NSafeFalsifier.falsify(falsifier, new NShrinkableValue<T>(shrunkValue, shrinker))) //
				.filter(Optional::isPresent) //
				.map(Optional::get) //
				.collect(Collectors.toSet());
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public int distance() {
		return shrinker.distance(value);
	}

	@Override
	public String toString() {
		return String.format("ShrinkableValue[%s:%d]", value(), distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		NShrinkableValue<?> that = (NShrinkableValue<?>) o;
		return distance() == that.distance() && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
