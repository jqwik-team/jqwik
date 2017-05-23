package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public interface NShrinkable<T> {

	Set<NShrinkable<T>> shrinkingCandidates();

	default NShrinkResult<NShrinkable<T>> shrink(Predicate<T> falsifier, Throwable originalError) {
		return new NSingleValueShrinker<>(this, originalError).shrink(falsifier);
	}

	default boolean falsifies(Predicate<T> falsifier) {
		return falsifier.negate().test(value());
	}

	T value();

	int distance();

	default <U> NShrinkable<U> map(Function<T, U> mapper) {
		return new NMappedShrinkable<>(this, mapper);
	}
}
