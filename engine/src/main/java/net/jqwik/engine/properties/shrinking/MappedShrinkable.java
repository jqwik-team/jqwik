package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class MappedShrinkable<T, U> implements Shrinkable<U> {

	private final Shrinkable<T> toMap;
	private final Function<? super T, ? extends U> mapper;

	public MappedShrinkable(Shrinkable<T> toMap, Function<? super T, ? extends U> mapper) {
		this.toMap = toMap;
		this.mapper = mapper;
	}

	@Override
	public U value() {
		return mapper.apply(toMap.value());
	}

	@Override
	public Stream<Shrinkable<U>> shrink() {
		return toMap.shrink().map(this::toMappedShrinkable);
	}

	public Shrinkable<U> toMappedShrinkable(Shrinkable<T> shrinkable) {
		return shrinkable.map(mapper);
	}

	@Override
	public Optional<Shrinkable<U>> grow(Shrinkable<?> before, Shrinkable<?> after) {
		if (before instanceof MappedShrinkable && after instanceof MappedShrinkable) {
			Shrinkable<?> beforeToMap = ((MappedShrinkable<?, ?>) before).toMap;
			Shrinkable<?> afterToMap = ((MappedShrinkable<?, ?>) after).toMap;
			return toMap.grow(beforeToMap, afterToMap).map(this::toMappedShrinkable);
		}
		return toMap.grow(before, after).map(this::toMappedShrinkable);
	}

	@Override
	public Stream<Shrinkable<U>> grow() {
		return toMap.grow().map(this::toMappedShrinkable);
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
