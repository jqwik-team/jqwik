package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public interface Arbitrary<T> {

	Generator<T> generator(long seed, int tries);

	default List<T> shrink(T value) {
		return Collections.emptyList();
	}

	default Arbitrary<T> filter(Predicate<? super T> predicate) {
		return new Arbitrary<T>() {

			@Override
			public Generator<T> generator(long seed, int tries) {
				return Arbitrary.this.generator(seed, tries).filter(predicate);
			}

		};
	}

}
