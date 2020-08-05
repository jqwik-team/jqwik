package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

class PairwiseShrinker extends AbstractShrinker {

	public PairwiseShrinker(Map<List<Object>, TryExecutionResult> falsificationCache) {
		super(falsificationCache);
	}

	@Override
	public FalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		FalsifiedSample current = sample;
		List<Tuple.Tuple2<Integer, Integer>> allPairs = Combinatorics.distinctPairs(sample.size()).collect(Collectors.toList());
		for (Tuple.Tuple2<Integer, Integer> pair : allPairs) {
			current = shrinkPair(falsifier, current, shrinkSampleConsumer, shrinkAttemptConsumer, pair.get1(), pair.get2());
		}
		return current;
	}

	private FalsifiedSample shrinkPair(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer,
		int index1,
		int index2
	) {
		Function<List<Shrinkable<Object>>, Stream<List<Shrinkable<Object>>>> shrinker =
			shrinkables -> {
				Stream<Shrinkable<Object>> shrink1 = shrinkables.get(index1).shrink();
				Stream<Shrinkable<Object>> shrink2 = shrinkables.get(index2).shrink();

				return JqwikStreamSupport.zip(shrink1, shrink2, (shrinkable1, shrinkable2) -> {
					ArrayList<Shrinkable<Object>> newShrinkables = new ArrayList<>(sample.shrinkables());
					newShrinkables.set(index1, shrinkable1);
					newShrinkables.set(index2, shrinkable2);
					return newShrinkables;
				});
			};

		return shrink(
			falsifier,
			sample,
			shrinkSampleConsumer,
			shrinkAttemptConsumer,
			shrinker
		);
	}

}
