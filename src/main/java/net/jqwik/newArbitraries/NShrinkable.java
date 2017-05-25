package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public interface NShrinkable<T> {

	Set<NShrinkResult<NShrinkable<T>>> shrinkNext(Predicate<T> falsifier);

	default NShrinkResult<NShrinkable<T>> shrink(Predicate<T> falsifier, Throwable originalError) {
		return new NSingleValueShrinker<>(this, originalError).shrink(falsifier);
	}

	T value();

	int distance();

	default <U> NShrinkable<U> map(Function<T, U> mapper) {
		return new NMappedShrinkable<>(this, mapper);
	}
}
