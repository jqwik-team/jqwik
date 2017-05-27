package net.jqwik.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ParameterListShrinker<T> {

	private final List<Shrinkable<T>> parametersToShrink;

	public ParameterListShrinker(List<Shrinkable<T>> parametersToShrink) {
		this.parametersToShrink = parametersToShrink;
	}

	public ShrinkResult<List<Shrinkable<T>>> shrink(Predicate<List<T>> forAllFalsifier, Throwable originalError) {
		ArrayList<Shrinkable<T>> resultShrinkables = new ArrayList<>(parametersToShrink);

		Throwable[] lastFalsifiedError = new Throwable[] { originalError };
		for (int position = 0; position < parametersToShrink.size(); position++) {
			ShrinkResult<Shrinkable<T>> shrunkPositionResult = shrinkPosition(position, forAllFalsifier);
			shrunkPositionResult.throwable().ifPresent(throwable -> lastFalsifiedError[0] = throwable);
			resultShrinkables.set(position, shrunkPositionResult.shrunkValue());
		}
		return ShrinkResult.of(resultShrinkables, lastFalsifiedError[0]);
	}

	private ShrinkResult<Shrinkable<T>> shrinkPosition(int position, Predicate<List<T>> forAllFalsifier) {
		Shrinkable<T> currentShrinkable = parametersToShrink.get(position);
		Predicate<T> elementFalsifier = createFalsifierForPosition(position, forAllFalsifier);
		ValueShrinker<T> shrinker = new ValueShrinker<>(currentShrinkable);
		return shrinker.shrink(elementFalsifier, null);
	}

	public Set<ShrinkResult<List<Shrinkable<T>>>> shrinkNext(Predicate<List<T>> forAllFalsifier) {
		for (int i = 0; i < parametersToShrink.size(); i++) {
			Set<ShrinkResult<List<Shrinkable<T>>>> shrinkResults = shrinkPositionNext(i, forAllFalsifier);
			if (!shrinkResults.isEmpty())
				return shrinkResults;
		}
		return Collections.emptySet();
	}

	private Set<ShrinkResult<List<Shrinkable<T>>>> shrinkPositionNext(int position, Predicate<List<T>> forAllFalsifier) {
		Shrinkable<T> currentShrinkable = parametersToShrink.get(position);
		Predicate<T> elementFalsifier = createFalsifierForPosition(position, forAllFalsifier);
		Set<ShrinkResult<Shrinkable<T>>> shrinkParameterResults = currentShrinkable.shrinkNext(elementFalsifier);
		return shrinkParameterResults.stream() //
				.map(shrinkParameterResult -> shrinkParameterResult //
						.map(shrinkable -> {
							List<Shrinkable<T>> newParameters = new ArrayList<>(parametersToShrink);
							newParameters.set(position, shrinkable);
							return newParameters;
						})) //
				.collect(Collectors.toSet());
	}

	private Predicate<T> createFalsifierForPosition(int position, Predicate<List<T>> forAllFalsifier) {
		return param -> {
			List<T> effectiveParams = parametersToShrink.stream().map(Shrinkable::value).collect(Collectors.toList());
			effectiveParams.set(position, param);
			return forAllFalsifier.test(effectiveParams);
		};
	}

}
