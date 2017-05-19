package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public interface NShrinkableGenerator<T> {
	NShrinkable<T> next(Random random);

	default <U> NShrinkableGenerator<U> map(Function<T, U> mapper) {
		return random -> new NMappedShrinkable<>(this.next(random), mapper);
	}

	default NShrinkableGenerator<T> filter(Predicate<T> filterPredicate) {
		return new NFilteredGenerator<>(this, filterPredicate);
	}

}
