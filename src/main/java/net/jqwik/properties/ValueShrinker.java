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
		if (toTry.isEmpty()) return allFalsified;
		Set<ShrinkResult<Shrinkable<T>>> toTryNext = new HashSet<>();
		ShrinkingHelper.minDistanceStream(toTry) //
			.limit(10) // This is a more or less random value to constrain the number of options considered
			.forEach(shrinkResult -> {
				Optional<ShrinkResult<Shrinkable<T>>> falsifyResult = SafeFalsifier.falsify(falsifier, shrinkResult.shrunkValue());
				falsifyResult.ifPresent(result -> {
					allFalsified.add(result);
					toTryNext.addAll(result.shrunkValue().shrinkNext(falsifier));
				});
			});
		return collectAllFalsified(toTryNext, allFalsified, falsifier);
	}

	private int minDistance(Set<ShrinkResult<Shrinkable<T>>> toTry) {
		int minDistance = Integer.MAX_VALUE;
		for (ShrinkResult<Shrinkable<T>> shrinkResult : toTry) {
			int distance = shrinkResult.shrunkValue().distance();
			if (distance < minDistance) minDistance = distance;
		}
		return minDistance;
	}

}
