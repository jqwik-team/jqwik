package net.jqwik.api;

import java.util.function.*;

public interface ExhaustiveGenerator<T> extends Iterable<T> {

	abstract class ExhaustiveGeneratorFacade {
		private static final String EXHAUSTIVE_GENERATOR_FACADE_IMPL = "net.jqwik.engine.facades.ExhaustiveGeneratorFacadeImpl";
		private static ExhaustiveGeneratorFacade implementation;

		static  {
			try {
				implementation = (ExhaustiveGeneratorFacade) Class.forName(EXHAUSTIVE_GENERATOR_FACADE_IMPL).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public abstract <T, U> ExhaustiveGenerator<U> map(ExhaustiveGenerator<T> self, Function<T, U> mapper);

		public abstract <T> ExhaustiveGenerator<T> filter(ExhaustiveGenerator<T> self, Predicate<T> filterPredicate);

		public abstract <T> ExhaustiveGenerator<T> unique(ExhaustiveGenerator<T> self);

		public abstract <T> ExhaustiveGenerator<T> injectNull(ExhaustiveGenerator<T> self);

		public abstract <T> ExhaustiveGenerator<T> withSamples(ExhaustiveGenerator<T> self, T[] samples);
	}

	/**
	 * @return the maximum number of values that will be generated
	 */
	long maxCount();

	default <U> ExhaustiveGenerator<U> map(Function<T, U> mapper) {
		return ExhaustiveGeneratorFacade.implementation.map(this, mapper);
	}

	default ExhaustiveGenerator<T> filter(Predicate<T> filterPredicate) {
		return ExhaustiveGeneratorFacade.implementation.filter(this, filterPredicate);
	}

	/**
	 * This is a hack to make unique work for exhaustive generation
	 */
	default boolean isUnique() {
		return false;
	}

	default ExhaustiveGenerator<T> unique() {
		return ExhaustiveGeneratorFacade.implementation.unique(this);
	}

	default ExhaustiveGenerator<T> injectNull() {
		return ExhaustiveGeneratorFacade.implementation.injectNull(this);
	}

	default ExhaustiveGenerator<T> withSamples(T[] samples) {
		return ExhaustiveGeneratorFacade.implementation.withSamples(this, samples);
	}

}
