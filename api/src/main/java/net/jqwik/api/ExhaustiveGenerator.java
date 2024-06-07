package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Used only internally to run and compute exhaustive generation of parameters
 */
@API(status = INTERNAL)
public interface ExhaustiveGenerator<T extends @Nullable Object> extends Iterable<T> {

	long MAXIMUM_SAMPLES_TO_GENERATE = Integer.MAX_VALUE;

	@API(status = INTERNAL)
	abstract class ExhaustiveGeneratorFacade {
		private static final ExhaustiveGeneratorFacade implementation;

		static {
			implementation = FacadeLoader.load(ExhaustiveGeneratorFacade.class);
		}

		public abstract <T extends @Nullable Object, U extends @Nullable Object> ExhaustiveGenerator<U> map(ExhaustiveGenerator<T> self, Function<? super T, ? extends U> mapper);

		public abstract <T extends @Nullable Object> ExhaustiveGenerator<T> filter(ExhaustiveGenerator<T> self, Predicate<? super T> filterPredicate, int maxMisses);

		public abstract <T extends @Nullable Object> ExhaustiveGenerator<@Nullable T> injectNull(ExhaustiveGenerator<T> self);

		public abstract <T extends @Nullable Object> ExhaustiveGenerator<T> ignoreExceptions(
			ExhaustiveGenerator<T> self,
			Class<? extends Throwable>[] exceptionTypes,
			int maxThrows
		);
	}

	/**
	 * @return the maximum number of values that will be generated
	 */
	long maxCount();

	default <U extends @Nullable Object> ExhaustiveGenerator<U> map(Function<? super T, ? extends U> mapper) {
		return ExhaustiveGeneratorFacade.implementation.map(this, mapper);
	}

	default ExhaustiveGenerator<T> filter(Predicate<? super T> filterPredicate, int maxMisses) {
		return ExhaustiveGeneratorFacade.implementation.filter(this, filterPredicate, maxMisses);
	}

	default ExhaustiveGenerator<T> injectNull() {
		return ExhaustiveGeneratorFacade.implementation.injectNull(this);
	}

	default ExhaustiveGenerator<T> ignoreExceptions(int maxThrows, Class<? extends Throwable>[] exceptionTypes) {
		return ExhaustiveGeneratorFacade.implementation.ignoreExceptions(this, exceptionTypes, maxThrows);
	}

}
