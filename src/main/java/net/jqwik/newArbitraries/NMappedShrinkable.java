package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NMappedShrinkable<T, U> implements NShrinkable<U> {

	private final NShrinkable<T> toMap;
	private final Function<T, U> mapper;
	private final U value;

	public NMappedShrinkable(NShrinkable<T> toMap, Function<T, U> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
		this.value = mapper.apply(toMap.value());
	}

	@Override
	public Set<NShrinkResult<NShrinkable<U>>> shrinkNext(Predicate<U> falsifier) {
		Predicate<T> toMapPredicate = aT -> falsifier.test(mapper.apply(aT));
		return toMap.shrinkNext(toMapPredicate).stream() //
				.map(shrinkResult -> shrinkResult.map(shrunkValue -> (NShrinkable<U>) new NMappedShrinkable<>(shrunkValue, mapper))) //
				.collect(Collectors.toSet());
	}

	@Override
	public U value() {
		return value;
	}

	@Override
	public int distance() {
		return toMap.distance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof NShrinkable))
			return false;
		NShrinkable<?> that = (NShrinkable<?>) o;
		return Objects.equals(value, that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return String.format("MappedShrinkable[%s:%d]", value, distance());
	}
}
