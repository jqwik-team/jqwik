package net.jqwik.properties;

import net.jqwik.api.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import java.util.*;
import java.util.function.*;

public class ValueShrinker<T> {
	private final static int BOUNDED_SHRINK_DEPTH = 1000;

	private final Shrinkable<T> shrinkable;
	private final Consumer<ReportEntry> reporter;
	private final ShrinkingMode shrinkingMode;

	public ValueShrinker(Shrinkable<T> shrinkable, Consumer<ReportEntry> reporter, ShrinkingMode shrinkingMode) {
		this.shrinkable = shrinkable;
		this.reporter = reporter;
		this.shrinkingMode = shrinkingMode;
	}

	public ShrinkResult<Shrinkable<T>> shrink(Predicate<T> falsifier, Throwable originalError) {
		Set<ShrinkResult<Shrinkable<T>>> allFalsified = collectAllFalsified(shrinkable.shrinkNext(falsifier), new HashSet<>(), falsifier);
		return getBestShrunkValue(allFalsified, originalError);
	}

	private ShrinkResult<Shrinkable<T>> getBestShrunkValue(
		Set<ShrinkResult<Shrinkable<T>>> allFalsified, Throwable originalError
	) {
		return allFalsified
			.stream() //
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
		int shrinkSteps = 0;
		while (!toTry.isEmpty()) {
			shrinkSteps++;
			if (shrinkingMode == ShrinkingMode.BOUNDED && shrinkSteps > BOUNDED_SHRINK_DEPTH) {
				reportShrinkingBoundReached(BOUNDED_SHRINK_DEPTH, allFalsified);
				break;
			}
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

	private void reportShrinkingBoundReached(int steps, Set<ShrinkResult<Shrinkable<T>>> allFalsified) {
		ShrinkResult<Shrinkable<T>> best = getBestShrunkValue(allFalsified, null);
		String value = String.format(
			"%n    steps : %s%n    original value : %s%n    shrunk value   : %s%n",
			steps,
			shrinkable.value().toString(),
			best.shrunkValue().value()
		);
		reporter.accept(ReportEntry.from("shrinking bound reached", value));
	}

}
