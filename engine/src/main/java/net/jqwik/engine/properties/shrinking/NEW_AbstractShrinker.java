package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

abstract class NEW_AbstractShrinker {

	public abstract FalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	);

	protected FalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer,
		Function<List<Shrinkable<Object>>, Stream<List<Shrinkable<Object>>>> parameterShrinker
	) {
		List<Shrinkable<Object>> currentShrinkBase = sample.shrinkables();
		Optional<FalsifiedSample> bestResult = Optional.empty();

		while (true) {
			@SuppressWarnings("unchecked")
			Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>[] filteredResult = new Tuple3[]{null};
			ShrinkingDistance currentDistance = calculateDistance(currentShrinkBase);

			FalsifiedSample currentBest = bestResult.orElse(null);

			Optional<Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>> newShrinkingResult =
				parameterShrinker.apply(currentShrinkBase)
						.filter(shrinkables -> calculateDistance(shrinkables).compareTo(currentDistance) <= 0)
						.peek(ignore -> shrinkAttemptConsumer.accept(currentBest))
						.map(shrinkables -> {
							List<Object> params = createValues(shrinkables).collect(Collectors.toList());
							TryExecutionResult result = falsifier.execute(params);
							return Tuple.of(params, shrinkables, result);
						})
						.peek(t -> {
							// Remember best invalid result in case no  falsified shrink is found
							if (t.get3().isInvalid() && filteredResult[0] == null) {
								filteredResult[0] = t;
							}
						})
						.filter(t -> t.get3().isFalsified())
						.findFirst();

			if (newShrinkingResult.isPresent()) {
				Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult> falsifiedTry = newShrinkingResult.get();
				FalsifiedSample falsifiedSample = new FalsifiedSample(
					falsifiedTry.get1(),
					falsifiedTry.get2(),
					falsifiedTry.get3().throwable()
				);
				shrinkSampleConsumer.accept(falsifiedSample);
				bestResult = Optional.of(falsifiedSample);
				currentShrinkBase = falsifiedTry.get2();
			} else if (filteredResult[0] != null) {
				currentShrinkBase = filteredResult[0].get2();
			} else {
				break;
			}
		}

		return bestResult.orElse(sample);
	}

	private ShrinkingDistance calculateDistance(List<Shrinkable<Object>> shrinkables) {
		return ShrinkingDistance.forCollection(shrinkables);
	}

	private Stream<Object> createValues(List<Shrinkable<Object>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::createValue);
	}

}
