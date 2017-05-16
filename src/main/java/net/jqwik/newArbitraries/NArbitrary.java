package net.jqwik.newArbitraries;

import java.util.function.*;

public interface NArbitrary<T> {
	NShrinkableGenerator<T> generator(int tries);

	default NArbitrary<T> filter(Predicate<T> filterPredicate) {
		return tries -> new NFilteredGenerator<>(this.generator(tries), filterPredicate);
	}
}
