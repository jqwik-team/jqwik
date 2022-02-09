package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;

/**
 * Is loaded through reflection in api module
 */
public class ExhaustiveGeneratorFacadeImpl extends ExhaustiveGenerator.ExhaustiveGeneratorFacade {
	@Override
	public <T, U> ExhaustiveGenerator<U> map(ExhaustiveGenerator<? extends T> self, Function<? super T, ? extends U> mapper) {
		return new MappedExhaustiveGenerator<>(self, mapper);
	}

	@Override
	public <T> ExhaustiveGenerator<T> filter(ExhaustiveGenerator<? extends T> self, Predicate<? super T> filterPredicate, int maxMisses) {
		return new FilteredExhaustiveGenerator<>(self, filterPredicate, maxMisses);
	}

	@Override
	public <T> ExhaustiveGenerator<T> injectNull(ExhaustiveGenerator<? extends T> self) {
		return new WithNullExhaustiveGenerator<>(self);
	}

	@Override
	public <T> ExhaustiveGenerator<T> ignoreException(final ExhaustiveGenerator<? extends T> self, final Class<? extends Throwable> exceptionType) {
		return new IgnoreExceptionExhaustiveGenerator<>(self, exceptionType);
	}
}
