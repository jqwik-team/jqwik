package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class LazyOfShrinkable<T> implements Shrinkable<T> {
	public final Shrinkable<T> current;
	public final Set<LazyOfShrinkable<T>> parts;
	private final Function<LazyOfShrinkable<T>, Stream<Shrinkable<T>>> shrinker;

	public LazyOfShrinkable(
		Shrinkable<T> current,
		Set<LazyOfShrinkable<T>> parts,
		Function<LazyOfShrinkable<T>, Stream<Shrinkable<T>>> shrinker
	) {
		this.current = current;
		this.parts = parts;
		this.shrinker = shrinker;
	}

	@Override
	public T value() {
		return current.value();
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return shrinker.apply(this);
	}

	@Override
	public ShrinkingDistance distance() {
		return current.distance();
	}
}
