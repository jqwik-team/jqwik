package net.jqwik.newArbitraries;

import java.util.function.*;

public interface NArbitrary<T> {
	NShrinkableGenerator<T> generator(int tries);

	default NArbitrary<T> filter(Predicate<T> filterPredicate) {
		return tries -> new NFilteredGenerator<>(this.generator(tries), filterPredicate);
	}

	default <U> NArbitrary<U> map(Function<T, U> mapper) {
		return tries -> {
			NShrinkableGenerator<T> generator = this.generator(tries);
			return random -> new NMappedShrinkable<>(generator.next(random), mapper);
		};
	}
}
