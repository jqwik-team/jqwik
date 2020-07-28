package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

abstract class NEW_AbstractShrinker {

	private final Map<List<Object>, TryExecutionResult> falsificationCache;

	public NEW_AbstractShrinker(Map<List<Object>, TryExecutionResult> falsificationCache) {
		this.falsificationCache = falsificationCache;
	}

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
		Set<Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>> filteredResults = new HashSet<>();

		while (true) {
			ShrinkingDistance currentDistance = calculateDistance(currentShrinkBase);

			FalsifiedSample currentBest = bestResult.orElse(null);

			Optional<Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult>> newShrinkingResult =
				parameterShrinker.apply(currentShrinkBase)
						.filter(shrinkables -> calculateDistance(shrinkables).compareTo(currentDistance) <= 0)
						.peek(ignore -> shrinkAttemptConsumer.accept(currentBest))
						.map(shrinkables -> {
							List<Object> params = createValues(shrinkables).collect(Collectors.toList());
							TryExecutionResult result = falsify(falsifier, params);
							return Tuple.of(params, shrinkables, result);
						})
						.peek(t -> {
							// Remember best 10 invalid results in case no  falsified shrink is found
							if (t.get3().isInvalid() && filteredResults.size() < 10) {
								filteredResults.add(t);
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
			} else if (!filteredResults.isEmpty()) {
				Tuple3<List<Object>, List<Shrinkable<Object>>, TryExecutionResult> aFilteredResult = filteredResults.iterator().next();
				filteredResults.remove(aFilteredResult);
				currentShrinkBase = aFilteredResult.get2();
			} else {
				break;
			}
		}

		return bestResult.orElse(sample);
	}

	private TryExecutionResult falsify(Falsifier<List<Object>> falsifier, List<Object> params) {
		// I wonder in which cases this is really an optimization
		return falsificationCache.computeIfAbsent(params, p -> falsifier.execute(params));
	}

	private ShrinkingDistance calculateDistance(List<Shrinkable<Object>> shrinkables) {
		return ShrinkingDistance.forCollection(shrinkables);
	}

	private Stream<Object> createValues(List<Shrinkable<Object>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::createValue);
	}

}
