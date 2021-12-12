package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class PlainShrinker {

	private final Map<List<Object>, TryExecutionResult> falsificationCache = new HashMap<>();
	private final FalsifiedSample originalSample;
	private final Consumer<FalsifiedSample> shrinkSampleConsumer;
	private final Consumer<FalsifiedSample> shrinkAttemptConsumer;

	public PlainShrinker(
		FalsifiedSample originalSample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {

		this.originalSample = originalSample;
		this.shrinkSampleConsumer = shrinkSampleConsumer;
		this.shrinkAttemptConsumer = shrinkAttemptConsumer;
	}

	public FalsifiedSample shrink(final Falsifier<List<Object>> falsifier) {
		FalsifiedSample after = originalSample;
		FalsifiedSample before;
		do {
			before = after;
			after = shrinkOneParameterAfterTheOther(falsifier, before, shrinkSampleConsumer, shrinkAttemptConsumer);
			if (!after.equals(before)) {
				continue;
			}
			after = shrinkParametersPairwise(falsifier, after, shrinkSampleConsumer, shrinkAttemptConsumer);
			if (!after.equals(before)) {
				continue;
			}
			after = shrinkAndGrow(falsifier, after, shrinkSampleConsumer, shrinkAttemptConsumer);
		} while (!after.equals(before));
		return after;
	}

	private FalsifiedSample shrinkOneParameterAfterTheOther(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		return new OneAfterTheOtherParameterShrinker(falsificationCache)
				   .shrink(falsifier, sample, shrinkSampleConsumer, shrinkAttemptConsumer);
	}

	private FalsifiedSample shrinkParametersPairwise(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		return new PairwiseParameterShrinker(falsificationCache).shrink(falsifier, sample, shrinkSampleConsumer, shrinkAttemptConsumer);
	}

	private FalsifiedSample shrinkAndGrow(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		return new ShrinkAndGrowShrinker(falsificationCache).shrink(falsifier, sample, shrinkSampleConsumer, shrinkAttemptConsumer);
	}

}
