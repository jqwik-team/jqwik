package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NListShrinker<T> {
	private final List<NShrinkable<T>> shrinkables;
	private final Throwable originalError;

	public NListShrinker(List<NShrinkable<T>> shrinkables, Throwable originalError) {
		this.shrinkables = shrinkables;
		this.originalError = originalError;
	}

	public NShrinkResult<List<NShrinkable<T>>> shrink(Predicate<List<NShrinkable<T>>> falsifier) {
		Set<NShrinkResult<List<NShrinkable<T>>>> allFalsified = nextShrink(Collections.singleton(shrinkables), falsifier);
		return allFalsified.stream() //
				.sorted(Comparator.comparingInt(result -> result.value().size())) //
				.findFirst().orElse(NShrinkResult.of(shrinkables, originalError));
	}

	private Set<NShrinkResult<List<NShrinkable<T>>>> nextShrink(Set<List<NShrinkable<T>>> listsToShrink,
			Predicate<List<NShrinkable<T>>> falsifier) {
		return Collections.emptySet();
	}

	private Set<NShrinkResult<NShrinkable<T>>> collectAllFalsified(Set<NShrinkable<T>> toTry,
			Set<NShrinkResult<NShrinkable<T>>> allFalsified, Predicate<T> falsifier) {
		if (toTry.isEmpty())
			return allFalsified;
		Set<NShrinkable<T>> toTryNext = new HashSet<>();
		toTry.forEach(shrinkable -> {
			Optional<NShrinkResult<NShrinkable<T>>> falsifyResult = NSafeFalsifier.falsify(falsifier, shrinkable);
			falsifyResult.ifPresent(result -> {
				allFalsified.add(result);
				toTryNext.addAll(shrinkable.nextShrinkingCandidates());
			});
		});
		return collectAllFalsified(toTryNext, allFalsified, falsifier);
	}
}
