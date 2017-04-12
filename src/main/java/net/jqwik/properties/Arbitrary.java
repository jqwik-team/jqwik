package net.jqwik.properties;

import java.util.*;

public interface Arbitrary<T> {

	Generator<T> generator(long seed);

	default List<T> shrink(T value) {
		return Collections.emptyList();
	}
}
