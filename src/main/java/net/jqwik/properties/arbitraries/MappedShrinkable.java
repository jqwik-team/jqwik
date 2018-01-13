package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class MappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, U> mapper;
	private final U value;

	public MappedShrinkable(Shrinkable<T> toMap, Function<T, U> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
		this.value = mapper.apply(toMap.value());
	}

	@Override
	public Set<ShrinkResult<Shrinkable<U>>> shrinkNext(Predicate<U> falsifier) {
		Predicate<T> toMapPredicate = aT -> falsifier.test(mapper.apply(aT));
		return toMap.shrinkNext(toMapPredicate).stream() //
					.map(shrinkResult -> shrinkResult.map(shrunkValue -> (Shrinkable<U>) new MappedShrinkable<>(shrunkValue, mapper))) //
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
		if (o == null || !(o instanceof Shrinkable))
			return false;
		Shrinkable<?> that = (Shrinkable<?>) o;
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
