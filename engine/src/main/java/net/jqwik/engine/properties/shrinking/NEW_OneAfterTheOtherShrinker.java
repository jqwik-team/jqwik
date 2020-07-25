package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class NEW_OneAfterTheOtherShrinker {

	public FalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		AtomicInteger shrinkingStepsCounter
	) {
		FalsifiedSample current = sample;
		for (int i = 0; i < sample.size(); i++) {
			current = shrinkSingleParameter(falsifier, current, shrinkingStepsCounter, i);
		}
		return current;
	}

	private FalsifiedSample shrinkSingleParameter(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		AtomicInteger shrinkingStepsCounter,
		int parameterIndex
	) {
		Shrinkable<Object> currentShrinkBase = sample.shrinkables().get(parameterIndex);
		Optional<Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>> bestResult = Optional.empty();

		while (true) {
			@SuppressWarnings("unchecked")
			Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>[] filteredResult = new Tuple3[]{null};
			ShrinkingDistance currentDistance = currentShrinkBase.distance();

			Optional<Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>> newShrinkingResult =
				currentShrinkBase.shrink()
								 .filter(s -> s.distance().compareTo(currentDistance) < 0)
								 .map(s -> {
									 List<Object> params = replaceIn(s.createValue(), parameterIndex, sample.parameters());
									 List<Shrinkable<Object>> shrinkables = replaceIn(s, parameterIndex, sample.shrinkables());
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
				shrinkingStepsCounter.incrementAndGet();
				bestResult = newShrinkingResult;
				currentShrinkBase = bestResult.get().get2().get(parameterIndex);
			} else if (filteredResult[0] != null) {
				currentShrinkBase = filteredResult[0].get2().get(parameterIndex);
			} else {
				break;
			}
		}

		return bestResult
				   .map(t -> new FalsifiedSample(t.get1(), t.get2(), t.get3().throwable()))
				   .orElse(sample);
	}

	private <T> List<T> replaceIn(T object, int index, List<T> old) {
		List<T> newList = new ArrayList<>(old);
		newList.set(index, object);
		return newList;
	}

}
