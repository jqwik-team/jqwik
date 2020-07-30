package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;

public class FixedValueFlatMappedShrinkable<T, U> extends FlatMappedShrinkable<T, U> {

	private final Supplier<Shrinkable<U>> shrinkableSupplier;

	public FixedValueFlatMappedShrinkable(
		Shrinkable<T> toMap,
		Function<T, Shrinkable<U>> mapper,
		Supplier<Shrinkable<U>> shrinkableSupplier
	) {
		super(toMap, mapper);
		this.shrinkableSupplier = shrinkableSupplier;
	}

	@Override
	protected Shrinkable<U> shrinkable() {
		return shrinkableSupplier.get();
	}
}
