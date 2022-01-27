package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

class ShrinkAndGrowShrinker extends AbstractSampleShrinker {

	public ShrinkAndGrowShrinker(Map<List<Object>, TryExecutionResult> falsificationCache) {
		super(falsificationCache);
	}

	@Override
	public FalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> sampleShrunkConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		FalsifiedSample current = sample;
		List<Tuple.Tuple2<Integer, Integer>> allPairs =
			Combinatorics.distinctPairs(sample.shrinkables().size()).collect(Collectors.toList());
		for (Tuple.Tuple2<Integer, Integer> pair : allPairs) {
			current = shrinkAndGrow(falsifier, current, sampleShrunkConsumer, shrinkAttemptConsumer, pair.get1(), pair.get2());
		}
		return current;
	}

	private FalsifiedSample shrinkAndGrow(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> sampleShrunkConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer,
		int index1,
		int index2
	) {
		Function<List<Shrinkable<Object>>, Stream<List<Shrinkable<Object>>>> shrinker =
			shrinkables -> {
				Shrinkable<Object> before = shrinkables.get(index1);
				Stream<Shrinkable<Object>> afterStream = before.shrink();
				return afterStream.flatMap(after -> {
					Optional<Shrinkable<Object>> optionalShrink2 = shrinkables.get(index2).grow(before, after);
					if (optionalShrink2.isPresent()) {
						ArrayList<Shrinkable<Object>> newShrinkables = new ArrayList<>(sample.shrinkables());
						newShrinkables.set(index1, after);
						newShrinkables.set(index2, optionalShrink2.get());
						return Stream.of(newShrinkables);
					} else {
						return Stream.empty();
					}
				});
			};

		return shrink(
			falsifier,
			sample,
			sampleShrunkConsumer,
			shrinkAttemptConsumer,
			shrinker
		);
	}

}
