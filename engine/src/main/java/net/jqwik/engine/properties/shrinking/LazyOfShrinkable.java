package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public class LazyOfShrinkable<T extends @Nullable Object> implements Shrinkable<T> {
	public final Shrinkable<T> current;
	public final int depth;
	public final Set<LazyOfShrinkable<T>> parts;
	private final Function<LazyOfShrinkable<T>, Stream<Shrinkable<T>>> shrinker;

	public LazyOfShrinkable(
		Shrinkable<T> current,
		int depth,
		Set<LazyOfShrinkable<T>> parts,
		Function<LazyOfShrinkable<T>, Stream<Shrinkable<T>>> shrinker
	) {
		this.current = current;
		this.depth = depth;
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
		return ShrinkingDistance.of(depth).append(current.distance());
	}
}
