package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class MappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<T, U> mapper;

	public MappedShrinkable(Shrinkable<T> toMap, Function<T, U> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
	}

	@Override
	public U value() {
		return mapper.apply(toMap.value());
	}

	@Override
	public U createValue() {
		return value();
	}

	@Override
	public Stream<Shrinkable<U>> shrink() {
		return toMap.shrink().map(shrinkable -> shrinkable.map(mapper));
	}

	@Override
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		Falsifier<T> toMapFalsifier = falsifier.map(mapper);
		return toMap.shrink(toMapFalsifier)
					.map(result -> result.map(shrinkable -> shrinkable.map(mapper)));
	}

	@Override
	public List<Shrinkable<U>> shrinkingSuggestions() {
		return toMap.shrinkingSuggestions()
					.stream()
					.map(shrinkable -> shrinkable.map(mapper))
					.collect(Collectors.toList());
	}

	@Override
	public ShrinkingDistance distance() {
		return toMap.distance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MappedShrinkable<?, ?> that = (MappedShrinkable<?, ?>) o;
		return toMap.equals(that.toMap);
	}

	@Override
	public int hashCode() {
		return toMap.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Mapped<%s>(%s)|%s", value().getClass().getSimpleName(), value(), toMap);
	}

}
