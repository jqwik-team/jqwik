package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

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
	public Set<NShrinkable<U>> shrink() {
		return null;
	}

	@Override
	public boolean falsifies(Predicate<U> falsifier) {
		return falsifier.negate().test(value);
	}

	@Override
	public U value() {
		return value;
	}

	@Override
	public int distance() {
		return toMap.distance();
	}
}
