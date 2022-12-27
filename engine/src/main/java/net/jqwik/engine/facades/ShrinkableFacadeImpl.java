package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.random.*;
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
	public <T, U> Shrinkable<U> map(Shrinkable<T> self, Function<T, U> mapper) {
		return new MappedShrinkable<>(self, mapper);
	}

	@Override
	public <T> Shrinkable<T> filter(Shrinkable<T> self, Predicate<T> filter) {
		return new FilteredShrinkable<>(self, filter);
	}

	@Override
	public <T, U> Shrinkable<U> flatMap(Shrinkable<T> self, Function<T, Arbitrary<U>> flatMapper, int tries, JqwikRandomState randomSeed) {
		return new FlatMappedShrinkable<>(self, flatMapper, tries, randomSeed, false);
	}
}
