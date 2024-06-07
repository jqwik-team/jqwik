package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import org.jspecify.annotations.*;

class OneAfterTheOtherParameterShrinker extends AbstractSampleShrinker {

	public OneAfterTheOtherParameterShrinker(Map<List<Object>, TryExecutionResult> falsificationCache) {
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
		for (int i = 0; i < sample.shrinkables().size(); i++) {
			current = shrinkSingleParameter(falsifier, current, sampleShrunkConsumer, shrinkAttemptConsumer, i);
		}
		return current;
	}

	private FalsifiedSample shrinkSingleParameter(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> sampleShrunkConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer,
		int parameterIndex
	) {
		Function<List<Shrinkable<Object>>, Stream<List<Shrinkable<Object>>>> shrinker =
			shrinkables -> {
				Shrinkable<Object> shrinkable = shrinkables.get(parameterIndex);
				return shrinkable.shrink().map(s -> replaceIn(s, parameterIndex, sample.shrinkables()));
			};

		return shrink(
			falsifier,
			sample,
			sampleShrunkConsumer,
			shrinkAttemptConsumer,
			shrinker
		);
	}

	private <T extends @Nullable Object> List<T> replaceIn(T object, int index, List<T> old) {
		List<T> newList = new ArrayList<>(old);
		newList.set(index, object);
		return newList;
	}

}
