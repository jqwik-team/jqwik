package net.jqwik.api;

import java.util.function.*;

import net.jqwik.properties.arbitraries.exhaustive.*;

public interface ExhaustiveGenerator<T> extends Iterable<T> {

	/**
	 * @return the maximum number of values that will be generated
	 */
	long maxCount();

	default <U> ExhaustiveGenerator<U> map(Function<T, U> mapper) {
		return new MappedExhaustiveGenerator<>(this, mapper);
	}

	default ExhaustiveGenerator<T> filter(Predicate<T> filterPredicate) {
		return new FilteredExhaustiveGenerator<>(this, filterPredicate);
	}

	/**
	 * This is a hack to make unique work for exhaustive generation
	 */
	default boolean isUnique() {
		return false;
	}

	default ExhaustiveGenerator<T> unique() {
		return new UniqueExhaustiveGenerator<>(this);
	}

	default ExhaustiveGenerator<T> injectNull() {
		return new WithNullExhaustiveGenerator<>(this);
	}

	default ExhaustiveGenerator<T> withSamples(T[] samples) {
		return new WithSamplesExhaustiveGenerator<>(this, samples);
	}

	default <U> ExhaustiveGenerator<U> flatMap(Function<T, Arbitrary<U>> mapper) {
		return null;
//		return new FlatMappedExhaustiveGenerator(this, mapper);
	}
}
