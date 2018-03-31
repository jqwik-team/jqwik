package net.jqwik.properties;

import net.jqwik.api.*;
import org.opentest4j.*;

import java.util.*;
import java.util.function.*;

public class ValueShrinker<T> {
	private final Shrinkable<T> shrinkable;

	public ValueShrinker(Shrinkable<T> shrinkable) {
		this.shrinkable = shrinkable;
	}

	public ShrinkResult<Shrinkable<T>> shrink(Predicate<T> falsifier, Throwable originalError) {
		Set<ShrinkResult<Shrinkable<T>>> allFalsified = collectAllFalsified(shrinkable.shrinkNext(falsifier), new HashSet<>(), falsifier);
		return allFalsified.stream() //
			.filter(shrinkResult -> !isViolatedAssumption(shrinkResult)) //
			.sorted(Comparator.comparingInt(result -> result.shrunkValue().distance())) //
			.findFirst().orElse(ShrinkResult.of(shrinkable, originalError));
	}

	private boolean isViolatedAssumption(ShrinkResult<Shrinkable<T>> shrinkResult) {
		return shrinkResult.throwable().isPresent() && shrinkResult.throwable().get().getClass().isAssignableFrom(TestAbortedException.class);
	}

	private Set<ShrinkResult<Shrinkable<T>>> collectAllFalsified(
		Set<ShrinkResult<Shrinkable<T>>> toTry, Set<ShrinkResult<Shrinkable<T>>> allFalsified, Predicate<T> falsifier
	) {
		// This was originally a (tail) recursive call.
		// Converted to to iteration in order to prevent stack overflow
		while (!toTry.isEmpty()) {
			toTry.removeAll(allFalsified);
			Set<ShrinkResult<Shrinkable<T>>> toTryNext = new HashSet<>();
			ShrinkingHelper
				.minDistanceStream(toTry) //
				.limit(10) // This is a more or less random value to constrain the number of options considered
				.forEach(shrinkResult -> {
					allFalsified.add(shrinkResult);
					toTryNext.addAll(shrinkResult.shrunkValue().shrinkNext(falsifier));
				});
			toTry = toTryNext;
		}
		return allFalsified;
	}

}
