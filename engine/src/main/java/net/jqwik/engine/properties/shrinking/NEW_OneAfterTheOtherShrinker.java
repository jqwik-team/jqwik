package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class NEW_OneAfterTheOtherShrinker {

	public FalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		FalsifiedSample current = sample;
		for (int i = 0; i < sample.size(); i++) {
			current = shrinkSingleParameter(falsifier, current, shrinkSampleConsumer, shrinkAttemptConsumer, i);
		}
		return current;
	}

	private FalsifiedSample shrinkSingleParameter(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer,
		int parameterIndex
	) {
		Shrinkable<Object> currentShrinkBase = sample.shrinkables().get(parameterIndex);
		Optional<FalsifiedSample> bestResult = Optional.empty();

		while (true) {
			@SuppressWarnings("unchecked")
			Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>[] filteredResult = new Tuple3[]{null};
			ShrinkingDistance currentDistance = currentShrinkBase.distance();

			FalsifiedSample currentBest = bestResult.orElse(null);

			Optional<Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>> newShrinkingResult =
				currentShrinkBase.shrink()
								 .filter(s -> s.distance().compareTo(currentDistance) < 0)
								 .peek(ignore -> shrinkAttemptConsumer.accept(currentBest))
								 .map(s -> {
									 List<Shrinkable<Object>> shrinkables = replaceIn(s, parameterIndex, sample.shrinkables());
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
				currentShrinkBase = falsifiedTry.get2().get(parameterIndex);
			} else if (filteredResult[0] != null) {
				currentShrinkBase = filteredResult[0].get2().get(parameterIndex);
			} else {
				break;
			}
		}

		return bestResult.orElse(sample);
	}

	private Stream<Object> createValues(List<Shrinkable<Object>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::createValue);
	}

	private <T> List<T> replaceIn(T object, int index, List<T> old) {
		List<T> newList = new ArrayList<>(old);
		newList.set(index, object);
		return newList;
	}

}
