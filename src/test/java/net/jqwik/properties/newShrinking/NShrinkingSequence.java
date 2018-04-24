package net.jqwik.properties.newShrinking;

import org.opentest4j.*;

import java.util.*;

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
		Set<NShrinkable<T>> candidates = searchBase.shrink();
		Optional<FalsificationResult<T>> newBest = candidates
			.stream()
			.sorted()
			.map(this::falsify)
			.filter(result -> result.status() != FalsificationResult.Status.NOT_FALSIFIED)
			.findFirst();

		lastStepSuccessful = newBest.isPresent();
		if (lastStepSuccessful) {
			count.run();
			searchBase = newBest.get().shrinkable();
			if (newBest.get().status() == FalsificationResult.Status.FALSIFIED) {
				this.currentBest = newBest.get().shrinkable();
			}
		}
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
