package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

import org.opentest4j.*;

public class NValueShrinker<T> {
	private final NShrinkable<T> shrinkable;
	private final Throwable originalError;

	public NValueShrinker(NShrinkable<T> shrinkable, Throwable originalError) {
		this.shrinkable = shrinkable;
		this.originalError = originalError;
	}

	public NShrinkResult<NShrinkable<T>> shrink(Predicate<T> falsifier) {
		Set<NShrinkResult<NShrinkable<T>>> allFalsified = collectAllFalsified(shrinkable.shrinkNext(falsifier), new HashSet<>(), falsifier);
		return allFalsified.stream() //
				.filter(shrinkResult -> !isViolatedAssumption(shrinkResult)) //
				.sorted(Comparator.comparingInt(result -> result.shrunkValue().distance())) //
				.findFirst().orElse(NShrinkResult.of(shrinkable, originalError));
	}

	private boolean isViolatedAssumption(NShrinkResult<NShrinkable<T>> shrinkResult) {
		return shrinkResult.throwable().isPresent()
				&& shrinkResult.throwable().get().getClass().isAssignableFrom(TestAbortedException.class);
	}

	private Set<NShrinkResult<NShrinkable<T>>> collectAllFalsified(Set<NShrinkResult<NShrinkable<T>>> toTry,
			Set<NShrinkResult<NShrinkable<T>>> allFalsified, Predicate<T> falsifier) {
		if (toTry.isEmpty())
			return allFalsified;
		Set<NShrinkResult<NShrinkable<T>>> toTryNext = new HashSet<>();
		toTry.forEach(shrinkResult -> {
			Optional<NShrinkResult<NShrinkable<T>>> falsifyResult = NSafeFalsifier.falsify(falsifier, shrinkResult.shrunkValue());
			falsifyResult.ifPresent(result -> {
				allFalsified.add(result);
				toTryNext.addAll(result.shrunkValue().shrinkNext(falsifier));
			});
		});
		return collectAllFalsified(toTryNext, allFalsified, falsifier);
	}

}
