package net.jqwik.engine.properties.shrinking;

import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class LazyOfShrinkable<T> implements Shrinkable<T> {
	public final Shrinkable<T> current;
	private final Supplier<Stream<Shrinkable<T>>> shrinker;

	public LazyOfShrinkable(Shrinkable<T> current, Supplier<Stream<Shrinkable<T>>> shrinker) {
		this.current = current;
		this.shrinker = shrinker;
	}

	@Override
	public T value() {
		return current.value();
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return shrinker.get();
	}

	@Override
	public ShrinkingDistance distance() {
		return current.distance();
	}
}
