package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;

public class FlatMappedEdgeCase<T, U> extends FlatMappedShrinkable<T, U> {

	private final Shrinkable<U> initialShrinkable;

	public FlatMappedEdgeCase(
		Shrinkable<T> toMap,
		Function<T, Shrinkable<U>> mapper,
		Shrinkable<U> initialShrinkable
	) {
		super(toMap, mapper);
		this.initialShrinkable = initialShrinkable;
	}

	@Override
	protected Shrinkable<U> shrinkable() {
		return initialShrinkable;
	}
}
