package net.jqwik.newArbitraries;

import java.util.function.*;

public class NSingleValueShrinker<T> {
	private final NShrinkable<T> shrinkable;

	public NSingleValueShrinker(NShrinkable<T> shrinkable) {
		this.shrinkable = shrinkable;
	}

	public T shrink(Predicate<T> falsifier) {
		return shrinkable.value();
	}
}
