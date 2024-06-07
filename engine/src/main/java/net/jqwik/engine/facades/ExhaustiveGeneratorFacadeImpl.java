package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;

import org.jspecify.annotations.*;

/**
 * Is loaded through reflection in api module
 */
public class ExhaustiveGeneratorFacadeImpl extends ExhaustiveGenerator.ExhaustiveGeneratorFacade {
	@Override
	public <T extends @Nullable Object, U extends @Nullable Object> ExhaustiveGenerator<U> map(ExhaustiveGenerator<T> self, Function<? super T, ? extends U> mapper) {
		return new MappedExhaustiveGenerator<>(self, mapper);
	}

	@Override
	public <T extends @Nullable Object> ExhaustiveGenerator<T> filter(ExhaustiveGenerator<T> self, Predicate<? super T> filterPredicate, int maxMisses) {
		return new FilteredExhaustiveGenerator<>(self, filterPredicate, maxMisses);
	}

	@Override
	public <T extends @Nullable Object> ExhaustiveGenerator<@Nullable T> injectNull(ExhaustiveGenerator<T> self) {
		return new WithNullExhaustiveGenerator<>(self);
	}

	@Override
	public <T extends @Nullable Object> ExhaustiveGenerator<T> ignoreExceptions(
		final ExhaustiveGenerator<T> self,
		final Class<? extends Throwable>[] exceptionTypes,
		int maxThrows
	) {
		return new IgnoreExceptionExhaustiveGenerator<>(self, exceptionTypes, maxThrows);
	}
}
