package net.jqwik.properties.newShrinking;

import java.util.*;

public class NShrinker<T> {
	private final Falsifier<T> falsifier;
	private NShrinkable<T> shrinkable;
	private boolean lastStepSuccessful = true;

	public NShrinker(NShrinkable<T> shrinkable, Falsifier<T> falsifier) {
		this.shrinkable = shrinkable;
		this.falsifier = falsifier;
	}

	public boolean next() {
		if (!lastStepSuccessful)
			return false;
		Set<NShrinkable<T>> candidates = shrinkable.shrink();
		Optional<NShrinkable<T>> newBest = candidates
			.stream()
			.sorted()
			.filter(candidate -> !falsifier.test(candidate.value()))
			.findFirst();
		lastStepSuccessful = newBest.isPresent();
		if (lastStepSuccessful) {
			this.shrinkable = newBest.get();
		}
		return lastStepSuccessful;
	}

	NShrinkable<T> currentBest() {
		return shrinkable;
	}
}
