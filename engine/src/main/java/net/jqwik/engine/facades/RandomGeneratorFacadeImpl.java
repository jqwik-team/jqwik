package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

import org.jspecify.annotations.*;

/**
 * Is loaded through reflection in api module
 */
public class RandomGeneratorFacadeImpl extends RandomGenerator.RandomGeneratorFacade {
	@Override
	public <T extends @Nullable Object, U extends @Nullable Object> Shrinkable<U> flatMap(Shrinkable<T> self, Function<? super T, ? extends RandomGenerator<U>> mapper, long nextLong) {
		return new FlatMappedShrinkable<>(self, mapper, nextLong);
	}

	@Override
	public <T extends @Nullable Object, U extends @Nullable Object> Shrinkable<U> flatMap(
		Shrinkable<T> self,
		Function<? super T, ? extends Arbitrary<U>> mapper,
		int genSize,
		long nextLong,
		boolean withEmbeddedEdgeCases
	) {
		return new FlatMappedShrinkable<>(self, mapper, genSize, nextLong, withEmbeddedEdgeCases);
	}

	@Override
	public <T extends @Nullable Object> RandomGenerator<T> filter(RandomGenerator<T> self, Predicate<? super T> filterPredicate, int maxMisses) {
		return new FilteredGenerator<>(self, filterPredicate, maxMisses);
	}

	@Override
	public <T extends @Nullable Object> RandomGenerator<T> withEdgeCases(RandomGenerator<T> self, int genSize, EdgeCases<T> edgeCases) {
		return RandomGenerators.withEdgeCases(self, genSize, edgeCases);
	}

	@Override
	public <T extends @Nullable Object> RandomGenerator<List<T>> collect(RandomGenerator<T> self, Predicate<? super List<? extends T>> until) {
		return new CollectGenerator<>(self, until);
	}

	@Override
	public <T extends @Nullable Object> RandomGenerator<T> injectDuplicates(RandomGenerator<T> self, double duplicateProbability) {
		return new InjectDuplicatesGenerator<>(self, duplicateProbability);
	}

	@Override
	public <T extends @Nullable Object> RandomGenerator<T> ignoreExceptions(RandomGenerator<T> self, Class<? extends Throwable>[] exceptionTypes, int maxThrows) {
		return new IgnoreExceptionGenerator<>(self, exceptionTypes, maxThrows);
	}
}
