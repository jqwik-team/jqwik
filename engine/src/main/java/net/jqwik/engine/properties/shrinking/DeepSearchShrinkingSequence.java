package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class DeepSearchShrinkingSequence<T> implements ShrinkingSequence<T> {
	private final Function<Shrinkable<T>, Set<Shrinkable<T>>> candidatesFor;
	private final Falsifier<T> falsifier;
	private FalsificationResult<T> currentBest;
	private FalsificationResult<T> searchBase;
	private boolean lastStepSuccessful = true;

	public DeepSearchShrinkingSequence(Shrinkable<T> startingShrinkable, Function<Shrinkable<T>, Set<Shrinkable<T>>> candidatesFor, Falsifier<T> falsifier) {
		this.currentBest = FalsificationResult.falsified(startingShrinkable);
		this.searchBase = currentBest;
		this.candidatesFor = candidatesFor;
		this.falsifier = falsifier;
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
		if (!lastStepSuccessful)
			return false;

		lastStepSuccessful = false;

		Set<Shrinkable<T>> candidates = getShrinkableCandidates();

		List<FalsificationResult<T>> nextBase = candidates
			.stream()
			.sorted()
			.map(this::falsify)
			.filter(result -> result.status() != FalsificationResult.Status.VERIFIED)
			.collect(Collectors.toList());

		nextBase
			.stream()
			.filter(result -> result.status() == FalsificationResult.Status.FALSIFIED)
			.findFirst()
			.ifPresent(result -> {
				count.run();
				lastStepSuccessful = true;
				this.currentBest = result;
				falsifiedReporter.accept(this.currentBest);
				this.searchBase = this.currentBest;
			});

		nextBase
			.stream()
			.filter(result -> result.status() == FalsificationResult.Status.FILTERED_OUT)
			.filter(result -> result.shrinkable().isSmallerThan(currentBest.shrinkable())).filter(result -> result.shrinkable().isSmallerThan(searchBase.shrinkable()))
			.findFirst()
			.ifPresent(result -> {
				count.run();
				lastStepSuccessful = true;
				searchBase = result;
			});

		return lastStepSuccessful;
	}

	private Set<Shrinkable<T>> getShrinkableCandidates() {
		Set<Shrinkable<T>> candidates = new HashSet<>(candidatesFor.apply(searchBase.shrinkable()));
		if (searchBase != currentBest) {
			candidates.addAll(candidatesFor.apply(currentBest.shrinkable()));
		}
		return candidates;
	}

	private FalsificationResult<T> falsify(Shrinkable<T> candidate) {
		return falsifier.falsify(candidate);
	}

	@Override
	public FalsificationResult<T> current() {
		return currentBest;
	}

	@Override
	public void init(FalsificationResult<T> initialCurrent) {
		currentBest = FalsificationResult.falsified(currentBest.shrinkable(), initialCurrent.throwable().orElse(null));
	}
}
