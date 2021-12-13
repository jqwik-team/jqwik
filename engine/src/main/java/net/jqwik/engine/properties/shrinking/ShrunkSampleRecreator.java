package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class ShrunkSampleRecreator {
	private final FalsifiedSample originalSample;

	public ShrunkSampleRecreator(FalsifiedSample originalSample) {
		this.originalSample = originalSample;
	}

	public ShrunkFalsifiedSample recreateFrom(List<TryExecutionResult.Status> shrinkingSequence) {
		List<TryExecutionResult.Status> recreatingSequence = new ArrayList<>(shrinkingSequence);
		Falsifier<List<Object>> recreatingFalsifier = falsifier(recreatingSequence);

		AtomicInteger shrinkingSteps = new AtomicInteger(0);
		ShrinkingAlgorithm plainShrinker = new ShrinkingAlgorithm(
			originalSample,
			ignore -> shrinkingSteps.incrementAndGet(),
			ignore -> {}
		);

		FalsifiedSample recreatedSample = plainShrinker.shrink(recreatingFalsifier);

		return new ShrunkFalsifiedSampleImpl(recreatedSample, shrinkingSteps.get());
	}

	private Falsifier<List<Object>> falsifier(List<TryExecutionResult.Status> recreatingSequence) {
		return ignore -> {
			if (!recreatingSequence.isEmpty()) {
				TryExecutionResult.Status next = recreatingSequence.remove(0);
				switch (next) {
					case SATISFIED:
						return TryExecutionResult.satisfied();
					case INVALID:
						return TryExecutionResult.invalid();
					case FALSIFIED:
						return TryExecutionResult.falsified(null);
				}
			}
			return TryExecutionResult.satisfied();
		};
	}
}
