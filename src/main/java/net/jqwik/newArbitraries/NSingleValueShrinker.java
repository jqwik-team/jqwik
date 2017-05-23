package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NSingleValueShrinker<T> {
	private final NShrinkable<T> shrinkable;
	private final Throwable originalError;

	public NSingleValueShrinker(NShrinkable<T> shrinkable, Throwable originalError) {
		this.shrinkable = shrinkable;
		this.originalError = originalError;
	}

	public NShrinkResult<NShrinkable<T>> shrink(Predicate<T> falsifier) {
		Set<NShrinkResult<NShrinkable<T>>> allFalsified = collectAllFalsified(shrinkable.shrinkingCandidates(), new HashSet<>(), falsifier);
		return allFalsified.stream() //
			.sorted(Comparator.comparingInt(result -> result.value().distance())) //
			.findFirst().orElse(NShrinkResult.of(shrinkable, originalError));
	}

	private Set<NShrinkResult<NShrinkable<T>>> collectAllFalsified(Set<NShrinkable<T>> toTry, Set<NShrinkResult<NShrinkable<T>>> allFalsified, Predicate<T> falsifier) {
		if (toTry.isEmpty()) return allFalsified;
		Set<NShrinkable<T>> toTryNext = new HashSet<>();
		toTry.forEach(shrinkable -> {
			try {
				if (shrinkable.falsifies(falsifier)) {
					allFalsified.add(NShrinkResult.of(shrinkable, null));
					toTryNext.addAll(shrinkable.shrinkingCandidates());
				}
			} catch (AssertionError assertionError) {
				allFalsified.add(NShrinkResult.of(shrinkable, assertionError));
				toTryNext.addAll(shrinkable.shrinkingCandidates());
			}
		});
		return collectAllFalsified(toTryNext, allFalsified, falsifier);
	}
}
