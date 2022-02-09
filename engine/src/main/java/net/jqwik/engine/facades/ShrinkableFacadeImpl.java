package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

/**
 * Is loaded through reflection in api module
 */
public class ShrinkableFacadeImpl extends Shrinkable.ShrinkableFacade {
	@Override
	public <T> Shrinkable<T> unshrinkable(Supplier<T> valueSupplier, ShrinkingDistance distance) {
		return new Unshrinkable<>(valueSupplier, distance);
	}

	@Override
	public <T, U> Shrinkable<U> map(Shrinkable<? extends T> self, Function<? super T, ? extends U> mapper) {
		return new MappedShrinkable<>(self, mapper);
	}

	@Override
	public <T> Shrinkable<T> filter(Shrinkable<? extends T> self, Predicate<? super T> filter) {
		return new FilteredShrinkable<>(self, filter);
	}

	@Override
	public <T, U> Shrinkable<U> flatMap(Shrinkable<? extends T> self, Function<? super T, ? extends Arbitrary<? extends U>> flatMapper, int tries, long randomSeed) {
		return new FlatMappedShrinkable<>(self, flatMapper, tries, randomSeed, false);
	}
}
