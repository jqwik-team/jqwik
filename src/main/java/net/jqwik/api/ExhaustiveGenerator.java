package net.jqwik.api;

import java.util.function.*;

import net.jqwik.properties.arbitraries.*;

public interface ExhaustiveGenerator<T> extends Iterable<T> {

	/**
	 * @return the maximum number of values that will be generated
	 */
	long maxCount();

	default <U> ExhaustiveGenerator<U> map(Function<T, U> mapper) {
		return new MappedExhaustiveGenerator<>(this, mapper);
	}

	default ExhaustiveGenerator filter(Predicate<T> filterPredicate) {
		return new FilteredExhaustiveGenerator<>(this, filterPredicate);
	}
}
