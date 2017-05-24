package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NListShrinker<T> {
	private final List<NShrinkable<T>> toShrink;
	private final Throwable originalError;
	private final NListShrinkCandidates<T> shrinkCandidates = new NListShrinkCandidates<>();

	public NListShrinker(List<NShrinkable<T>> toShrink, Throwable originalError) {
		this.toShrink = toShrink;
		this.originalError = originalError;
	}

	public NShrinkResult<List<NShrinkable<T>>> shrink(Predicate<List<NShrinkable<T>>> falsifier) {
		Set<List<NShrinkable<T>>> candidates = shrinkCandidates.nextCandidates(toShrink);
		Set<NShrinkResult<List<NShrinkable<T>>>> allFalsified = collectAllFalsified(candidates, new HashSet<>(), falsifier);
		return allFalsified.stream() //
				.sorted(Comparator.comparingInt(result -> shrinkCandidates.distance(result.value()))) //
				.findFirst().orElse(NShrinkResult.of(toShrink, originalError));
	}

	private Set<NShrinkResult<List<NShrinkable<T>>>> collectAllFalsified(Set<List<NShrinkable<T>>> toTry,
			Set<NShrinkResult<List<NShrinkable<T>>>> allFalsified, Predicate<List<NShrinkable<T>>> falsifier) {
		if (toTry.isEmpty())
			return allFalsified;
		Set<List<NShrinkable<T>>> toTryNext = new HashSet<>();
		toTry.forEach(listOfShrinkable -> {
			Optional<NShrinkResult<List<NShrinkable<T>>>> falsifyResult = NSafeFalsifier.falsify(falsifier, listOfShrinkable);
			falsifyResult.ifPresent(result -> {
				allFalsified.add(result);
				toTryNext.addAll(shrinkCandidates.nextCandidates(listOfShrinkable));
			});
		});
		return collectAllFalsified(toTryNext, allFalsified, falsifier);
	}
}
