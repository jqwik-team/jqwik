package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public interface Arbitrary<T> {

	RandomGenerator<T> generator(long seed, int tries);

	default List<T> shrink(T value) {
		return Collections.emptyList();
	}

	default Arbitrary<T> filter(Predicate<? super T> predicate) {
		return new Arbitrary<T>() {

			@Override
			public RandomGenerator<T> generator(long seed, int tries) {
				return Arbitrary.this.generator(seed, tries).filter(predicate);
			}

		};
	}

}
