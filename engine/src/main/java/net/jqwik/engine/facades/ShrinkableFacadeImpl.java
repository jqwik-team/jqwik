package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

import org.jspecify.annotations.*;

/**
 * Is loaded through reflection in api module
 */
public class ShrinkableFacadeImpl extends Shrinkable.ShrinkableFacade {
	@Override
	public <T extends @Nullable Object> Shrinkable<T> unshrinkable(Supplier<? extends T> valueSupplier, ShrinkingDistance distance) {
		return new Unshrinkable<>(valueSupplier, distance);
	}

	@Override
	public <T extends @Nullable Object, U extends @Nullable Object> Shrinkable<U> map(Shrinkable<T> self, Function<? super T, ? extends U> mapper) {
		return new MappedShrinkable<>(self, mapper);
	}

	@Override
	public <T extends @Nullable Object> Shrinkable<T> filter(Shrinkable<T> self, Predicate<? super T> filter) {
		return new FilteredShrinkable<>(self, filter);
	}

	@Override
	public <T extends @Nullable Object, U extends @Nullable Object> Shrinkable<U> flatMap(Shrinkable<T> self, Function<? super T, ? extends Arbitrary<U>> flatMapper, int tries, long randomSeed) {
		return new FlatMappedShrinkable<>(self, flatMapper, tries, randomSeed, false);
	}
}
