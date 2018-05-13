package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.function.*;

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
	public ShrinkingSequence<U> shrink(Falsifier<U> falsifier) {
		Falsifier<T> toMapFalsifier = aT -> falsifier.test(mapper.apply(aT));
		return toMap.shrink(toMapFalsifier) //
			.map(result -> result.map(shrinkable -> shrinkable.map(mapper)));
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
