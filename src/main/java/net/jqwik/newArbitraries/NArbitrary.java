package net.jqwik.newArbitraries;

import java.util.function.*;

public interface NArbitrary<T> {
	NShrinkableGenerator<T> generator(int tries);

	default NArbitrary<T> filter(Predicate<T> filterPredicate) {
		return tries -> new NFilteredGenerator<>(this.generator(tries), filterPredicate);
	}

	default <U> NArbitrary<U> map(Function<T, U> mapper) {
		return tries -> this.generator(tries).map(mapper);
	}

	static int defaultMaxFromTries(int tries) {
		return Math.max(tries / 2 - 3, 3);
	}

	static int defaultCollectionSizeFromTries(int tries) {
		return (int) Math.max(Math.round(Math.sqrt(tries)), 3);
	}
}
