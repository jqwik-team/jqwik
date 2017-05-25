package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NParameterListShrinker<T> {

	private final List<NShrinkable<T>> parametersToShrink;

	public NParameterListShrinker(List<NShrinkable<T>> parametersToShrink) {
		this.parametersToShrink = parametersToShrink;
	}

	public Set<NShrinkResult<List<NShrinkable<T>>>> shrinkNext(Predicate<List<T>> forAllFalsifier) {
		for (int i = 0; i < parametersToShrink.size(); i++) {
			Set<NShrinkResult<List<NShrinkable<T>>>> shrinkResults = shrinkPosition(i, forAllFalsifier);
			if (!shrinkResults.isEmpty())
				return shrinkResults;
		}
		return Collections.emptySet();
	}

	private Set<NShrinkResult<List<NShrinkable<T>>>> shrinkPosition(int position, Predicate<List<T>> forAllFalsifier) {
		NShrinkable<T> currentShrinkable = parametersToShrink.get(position);
		Predicate<T> elementFalsifier = createFalsifierForPosition(position, forAllFalsifier);
		Set<NShrinkResult<NShrinkable<T>>> shrinkParameterResults = currentShrinkable.shrinkNext(elementFalsifier);
		return shrinkParameterResults.stream() //
				.map(shrinkParameterResult -> shrinkParameterResult //
						.map(shrinkable -> {
							List<NShrinkable<T>> newParameters = new ArrayList<>(parametersToShrink);
							newParameters.set(position, shrinkable);
							return newParameters;
						})) //
				.collect(Collectors.toSet());
	}

	private Predicate<T> createFalsifierForPosition(int position, Predicate<List<T>> forAllFalsifier) {
		return param -> {
			List<T> effectiveParams = parametersToShrink.stream().map(NShrinkable::value).collect(Collectors.toList());
			effectiveParams.set(position, param);
			return forAllFalsifier.test(effectiveParams);
		};
	}

}
