package net.jqwik.properties.newShrinking;

import org.opentest4j.*;

import java.util.*;
import java.util.stream.*;

public class NShrinkingSequence<T> {
	private final Falsifier<T> falsifier;
	private NShrinkable<T> currentBest;
	private NShrinkable<T> searchBase;
	private boolean lastStepSuccessful = true;

	public NShrinkingSequence(NShrinkable<T> shrinkable, Falsifier<T> falsifier) {
		this.currentBest = shrinkable;
		this.searchBase = shrinkable;
		this.falsifier = falsifier;
	}

	public boolean next(Runnable count) {
		if (!lastStepSuccessful)
			return false;

		lastStepSuccessful = false;

		Set<NShrinkable<T>> candidates = searchBase.shrink();
		List<FalsificationResult<T>> nextBase = candidates
			.stream()
			.sorted()
			.map(this::falsify)
			.filter(result -> result.status() != FalsificationResult.Status.VERIFIED)
			.peek(result -> lastStepSuccessful = true)
			.collect(Collectors.toList());

		nextBase
			.stream()
			.filter(result -> result.status() == FalsificationResult.Status.FALSIFIED)
			.findFirst()
			.ifPresent(result -> {
				count.run();
				this.currentBest = result.shrinkable();
				this.searchBase = this.currentBest;
			});

		nextBase
			.stream()
			.filter(result -> result.status() == FalsificationResult.Status.FILTERED_OUT)
			.filter(result -> result.shrinkable().isSmallerThan(currentBest))
			.findFirst()
			.ifPresent(result -> {
				count.run();
				lastStepSuccessful = true;
				searchBase = result.shrinkable();
			});

		return lastStepSuccessful;
	}

	private FalsificationResult<T> falsify(NShrinkable<T> candidate) {
		try {
			boolean falsified = !falsifier.test(candidate.value());
			if (falsified) {
				return FalsificationResult.falsified(candidate, null);
			} else {
				return FalsificationResult.notFalsified(candidate);
			}
		} catch (TestAbortedException tae) {
			return FalsificationResult.filtered(candidate);
		} catch (Throwable throwable) {
			return FalsificationResult.falsified(candidate, throwable);
		}
	}

	NShrinkable<T> currentBest() {
		return currentBest;
	}
}
