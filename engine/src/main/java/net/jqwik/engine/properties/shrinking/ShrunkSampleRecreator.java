package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class ShrunkSampleRecreator {

	private final List<Shrinkable<Object>> shrinkables;

	public ShrunkSampleRecreator(List<Shrinkable<Object>> shrinkables) {
		this.shrinkables = shrinkables;
	}

	public Optional<List<Shrinkable<Object>>> recreateFrom(List<TryExecutionResult.Status> shrinkingSequence) {
		List<TryExecutionResult.Status> recreatingSequence = new ArrayList<>(shrinkingSequence);
		Falsifier<List<Object>> recreatingFalsifier = falsifier(recreatingSequence);

		FalsifiedSample originalSample = createFalsifiedSample();

		AtomicInteger shrinkingSteps = new AtomicInteger(0);
		FalsifiedSample[] currentBest = new FalsifiedSample[]{originalSample};
		Consumer<FalsifiedSample> sampleShrunkConsumer = shrunkSample -> {
			// Remember current best because shrinking can be interrupted with RecreationDone exception
			currentBest[0] = shrunkSample;
			shrinkingSteps.incrementAndGet();
		};
		ShrinkingAlgorithm plainShrinker = new ShrinkingAlgorithm(
			originalSample,
			sampleShrunkConsumer,
			ignore -> {}
		);

		try {
			// Shrunk falsified sample has already been grabbed in sampleShrunkConsumer
			FalsifiedSample ignore = plainShrinker.shrink(recreatingFalsifier);
		} catch (RecreationDone ignore) {}

		if (recreatingSequence.isEmpty()) {
			return Optional.of(currentBest[0].shrinkables());
		} else {
			return Optional.empty();
		}
	}

	private FalsifiedSample createFalsifiedSample() {
		List<Object> parameters = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return new FalsifiedSampleImpl(
			parameters,
			shrinkables,
			null,
			Collections.emptyList()
		);
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
			throw new RecreationDone();
		};
	}

	private static class RecreationDone extends RuntimeException {}
}
