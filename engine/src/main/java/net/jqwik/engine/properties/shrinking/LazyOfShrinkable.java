package net.jqwik.engine.properties.shrinking;

import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class LazyOfShrinkable<T> implements Shrinkable<T> {
	private final Shrinkable<T> current;

	public LazyOfShrinkable(Shrinkable<T> current) {
		this.current = current;
	}

	@Override
	public T value() {
		return current.value();
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return JqwikStreamSupport.concat(
			shrinkCurrent()//,
			// shrinkAlternatives()
		);
	}

	public Stream<Shrinkable<T>> shrinkCurrent() {
		return current.shrink().map(s -> new LazyOfShrinkable<>(s));
	}

	@Override
	public ShrinkingDistance distance() {
		return current.distance();
	}
}
