package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public class FixedValueFlatMappedShrinkable<T extends @Nullable Object, U extends @Nullable Object> extends FlatMappedShrinkable<T, U> {

	private final Supplier<? extends Shrinkable<U>> shrinkableSupplier;

	public FixedValueFlatMappedShrinkable(
		Shrinkable<T> toMap,
		Function<? super T, ? extends Shrinkable<U>> mapper,
		Supplier<? extends Shrinkable<U>> shrinkableSupplier
	) {
		super(toMap, mapper);
		this.shrinkableSupplier = shrinkableSupplier;
	}

	@Override
	protected Shrinkable<U> shrinkable() {
		return shrinkableSupplier.get();
	}
}
