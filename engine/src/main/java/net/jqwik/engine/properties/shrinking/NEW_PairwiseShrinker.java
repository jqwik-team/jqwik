package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

class NEW_PairwiseShrinker extends NEW_AbstractShrinker {

	@Override
	public FalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		FalsifiedSample current = sample;
		if (sample.size() == 2) {
			current = shrinkPair(falsifier, current, shrinkSampleConsumer, shrinkAttemptConsumer, 0, 1);
		}
		// for (int i = 0; i < sample.size(); i++) {
		// 	current = shrinkPair(falsifier, current, shrinkSampleConsumer, shrinkAttemptConsumer, i, j);
		// }
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
