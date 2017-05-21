package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NSingleValueShrinker<T> {
	private final NShrinkable<T> shrinkable;
	private final AssertionError originalError;

	public NSingleValueShrinker(NShrinkable<T> shrinkable, AssertionError originalError) {
		this.shrinkable = shrinkable;
		this.originalError = originalError;
	}

	//TODO: Return ShrinkResult to also transport assertion error
	public NShrinkResult<NShrinkable<T>> shrink(Predicate<T> falsifier) {
		Set<NShrinkResult<NShrinkable<T>>> allFalsified = collectAllFalsified(shrinkable.shrink(), new HashSet<>(), falsifier);
		return allFalsified.stream() //
			.sorted(Comparator.comparingInt(result -> result.value().distance())) //
			.findFirst().orElse(NShrinkResult.of(shrinkable, originalError));
	}

	private Set<NShrinkResult<NShrinkable<T>>> collectAllFalsified(Set<NShrinkable<T>> toTry, Set<NShrinkResult<NShrinkable<T>>> allFalsified, Predicate<T> falsifier) {
		if (toTry.isEmpty()) return allFalsified;
		Set<NShrinkable<T>> toTryNext = new HashSet<>();
		toTry.forEach(shrinkable -> {
			if (shrinkable.falsifies(falsifier)) {
				allFalsified.add(NShrinkResult.of(shrinkable, null));
				toTryNext.addAll(shrinkable.shrink());
			}
		});
		return collectAllFalsified(toTryNext, allFalsified, falsifier);
	}
}
