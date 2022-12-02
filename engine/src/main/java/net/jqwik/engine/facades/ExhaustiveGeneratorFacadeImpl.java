package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;

/**
 * Is loaded through reflection in api module
 */
public class ExhaustiveGeneratorFacadeImpl extends ExhaustiveGenerator.ExhaustiveGeneratorFacade {
	@Override
	public <T, U> ExhaustiveGenerator<U> map(ExhaustiveGenerator<T> self, Function<T, U> mapper) {
		return new MappedExhaustiveGenerator<>(self, mapper);
	}

	@Override
	public <T> ExhaustiveGenerator<T> filter(ExhaustiveGenerator<T> self, Predicate<T> filterPredicate, int maxMisses) {
		return new FilteredExhaustiveGenerator<>(self, filterPredicate, maxMisses);
	}

	@Override
	public <T> ExhaustiveGenerator<T> injectNull(ExhaustiveGenerator<T> self) {
		return new WithNullExhaustiveGenerator<>(self);
	}

	@Override
	public <T> ExhaustiveGenerator<T> ignoreExceptions(final ExhaustiveGenerator<T> self, final Class<? extends Throwable>[] exceptionTypes) {
		return new IgnoreExceptionExhaustiveGenerator<>(self, exceptionTypes);
	}
}
