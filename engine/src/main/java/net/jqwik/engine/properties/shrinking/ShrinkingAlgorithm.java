package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

class ShrinkingAlgorithm {

	private final Map<List<Object>, TryExecutionResult> falsificationCache = new LinkedHashMap<>();
	private final FalsifiedSample originalSample;
	private final Consumer<FalsifiedSample> sampleShrunkConsumer;
	private final Consumer<FalsifiedSample> shrinkAttemptConsumer;

	ShrinkingAlgorithm(
		FalsifiedSample originalSample,
		Consumer<FalsifiedSample> sampleShrunkConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {

		this.originalSample = originalSample;
		this.sampleShrunkConsumer = sampleShrunkConsumer;
		this.shrinkAttemptConsumer = shrinkAttemptConsumer;
	}

	FalsifiedSample shrink(final Falsifier<List<Object>> falsifier) {
		FalsifiedSample after = originalSample;
		FalsifiedSample before;
		do {
			before = after;
			after = shrinkOneParameterAfterTheOther(falsifier, before, sampleShrunkConsumer, shrinkAttemptConsumer);
			if (!after.equals(before)) {
				continue;
			}
			after = shrinkParametersPairwise(falsifier, after, sampleShrunkConsumer, shrinkAttemptConsumer);
			if (!after.equals(before)) {
				continue;
			}
			after = shrinkAndGrow(falsifier, after, sampleShrunkConsumer, shrinkAttemptConsumer);
		} while (!after.equals(before));
		return after;
	}

	private FalsifiedSample shrinkOneParameterAfterTheOther(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> sampleShrunkConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		return new OneAfterTheOtherParameterShrinker(falsificationCache)
				   .shrink(falsifier, sample, sampleShrunkConsumer, shrinkAttemptConsumer);
	}

	private FalsifiedSample shrinkParametersPairwise(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> sampleShrunkConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		return new PairwiseParameterShrinker(falsificationCache).shrink(falsifier, sample, sampleShrunkConsumer, shrinkAttemptConsumer);
	}

	private FalsifiedSample shrinkAndGrow(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> sampleShrunkConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		return new ShrinkAndGrowShrinker(falsificationCache).shrink(falsifier, sample, sampleShrunkConsumer, shrinkAttemptConsumer);
	}

}
