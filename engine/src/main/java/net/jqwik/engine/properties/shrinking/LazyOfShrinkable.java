package net.jqwik.engine.properties.shrinking;

import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class LazyOfShrinkable<T> implements Shrinkable<T> {
	private final Shrinkable<T> current;
	private final Supplier<Stream<Shrinkable<T>>> centralShrinker;

	public LazyOfShrinkable(Shrinkable<T> current, Supplier<Stream<Shrinkable<T>>> centralShrinker) {
		this.current = current;
		this.centralShrinker = centralShrinker;
	}

	@Override
	public T value() {
		return current.value();
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return shrinkCurrent();
		// return JqwikStreamSupport.concat(
		// 	shrinkCurrent(),
		// 	Stream.empty()
		// );
	}

	public Stream<Shrinkable<T>> shrinkCurrent() {
		return current.shrink().map(s -> new LazyOfShrinkable<>(s, centralShrinker));
	}

	@Override
	public ShrinkingDistance distance() {
		return current.distance();
	}
}
