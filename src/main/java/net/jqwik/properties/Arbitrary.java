package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public interface Arbitrary<T> {

	RandomGenerator<T> generator(int tries);

	// Not being used yet
	default List<T> shrink(T value) {
		return Collections.emptyList();
	}

	default Arbitrary<T> filter(Predicate<? super T> predicate) {
		return (tries) -> Arbitrary.this.generator(tries).filter(predicate);
	}

	/**
	 * Maps arbitrary objects T to arbitrary object U.
	 */
	default <U> Arbitrary<U> map(Function<? super T, ? extends U> mapper) {
		return (tries) -> Arbitrary.this.generator(tries).map(mapper);
	}

	default Arbitrary<T> injectNull(double nullProbability) {
		return (tries) -> Arbitrary.this.generator(tries).injectNull(nullProbability);
	}

}
