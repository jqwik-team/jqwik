package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.*;

public class NShrinkableValue<T> implements Shrinkable<T> {

	private final T value;
	private final NShrinkCandidates<T> shrinker;

	public NShrinkableValue(T value, NShrinkCandidates<T> shrinker) {
		this.value = value;
		this.shrinker = shrinker;
	}

	@Override
	public Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		return shrinker.nextCandidates(value).stream() //
					   .map(shrunkValue -> SafeFalsifier.falsify(falsifier, new NShrinkableValue<T>(shrunkValue, shrinker))) //
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
		if (o == null || !(o instanceof Shrinkable))
			return false;
		Shrinkable<?> that = (Shrinkable<?>) o;
		return Objects.equals(value, that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
