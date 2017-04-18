package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public interface Arbitrary<T> {

	RandomGenerator<T> generator(long seed, int tries);

	default List<T> shrink(T value) {
		return Collections.emptyList();
	}

	default Arbitrary<T> filter(Predicate<? super T> predicate) {
		return (seed, tries) -> Arbitrary.this.generator(seed, tries).filter(predicate);
	}

	/**
	 * Maps arbitrary objects T to arbitrary object U.
	 */
	default <U> Arbitrary<U> map(Function<? super T, ? extends U> mapper) {
		return (seed, tries) -> Arbitrary.this.generator(seed, tries).map(mapper);
	}

}
