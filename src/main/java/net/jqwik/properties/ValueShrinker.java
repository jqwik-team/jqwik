package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

import org.opentest4j.*;

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
		return shrinkResult.throwable().isPresent()
				&& shrinkResult.throwable().get().getClass().isAssignableFrom(TestAbortedException.class);
	}

	private Set<ShrinkResult<Shrinkable<T>>> collectAllFalsified(Set<ShrinkResult<Shrinkable<T>>> toTry,
																 Set<ShrinkResult<Shrinkable<T>>> allFalsified, Predicate<T> falsifier) {
		if (toTry.isEmpty())
			return allFalsified;
		Set<ShrinkResult<Shrinkable<T>>> toTryNext = new HashSet<>();
		toTry.forEach(shrinkResult -> {
			Optional<ShrinkResult<Shrinkable<T>>> falsifyResult = SafeFalsifier.falsify(falsifier, shrinkResult.shrunkValue());
			falsifyResult.ifPresent(result -> {
				allFalsified.add(result);
				toTryNext.addAll(result.shrunkValue().shrinkNext(falsifier));
			});
		});
		return collectAllFalsified(toTryNext, allFalsified, falsifier);
	}

}
