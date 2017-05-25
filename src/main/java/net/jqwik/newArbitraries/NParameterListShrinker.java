package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NParameterListShrinker<T> {

	private final Predicate<List<T>> forAllFalsifier;

	public NParameterListShrinker(Predicate<List<T>> forAllFalsifier) {
		this.forAllFalsifier = forAllFalsifier;
	}

	public Set<NShrinkResult<List<NShrinkable<T>>>> shrinkNext(List<NShrinkable<T>> originalParams) {
		// TODO: Only return the next shrinking step, not the full shrinking. Important for filtering!
		NShrinkResult<List<NShrinkable<T>>> shrinkResult = shrinkListElements(originalParams, null);
		if (originalParams.equals(shrinkResult.shrunkValue()))
			return Collections.emptySet();
		return Collections.singleton(shrinkResult);
	}

	private NShrinkResult<List<NShrinkable<T>>> shrinkListElements(List<NShrinkable<T>> originalParams, Throwable originalError) {

		Throwable[] lastFalsifiedError = { originalError };
		List<NShrinkable<T>> lastFalsifiedParams = new ArrayList<>(originalParams);
		for (int i = 0; i < originalParams.size(); i++) {
			shrinkPosition(i, lastFalsifiedParams, lastFalsifiedError);
		}

		return NShrinkResult.of(lastFalsifiedParams, lastFalsifiedError[0]);
	}

	private void shrinkPosition(int position, List<NShrinkable<T>> lastFalsifiedShrinkables, Throwable[] lastFalsifiedError) {
		NShrinkable<T> currentShrinkable = lastFalsifiedShrinkables.get(position);
		Predicate<T> elementFalsifier = createFalsifierForPosition(position, lastFalsifiedShrinkables);
		NShrinkResult<NShrinkable<T>> shrinkResult = new NSingleValueShrinker<>(currentShrinkable, null).shrink(elementFalsifier);

		lastFalsifiedShrinkables.set(position, shrinkResult.shrunkValue());
		shrinkResult.throwable().ifPresent(error -> lastFalsifiedError[0] = error);
	}

	private Predicate<T> createFalsifierForPosition(int position, List<NShrinkable<T>> lastFalsifiedParams) {
		return param -> {
			List<T> effectiveParams = lastFalsifiedParams.stream().map(NShrinkable::value).collect(Collectors.toList());
			effectiveParams.set(position, param);
			return forAllFalsifier.test(effectiveParams);
		};
	}

}
