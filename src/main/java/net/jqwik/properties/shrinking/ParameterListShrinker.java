package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.*;

public class ParameterListShrinker<T> {

	private final Function<Integer, Arbitrary<T>> arbitraryProvider;
	private final Predicate<List<T>> forAllFalsifier;

	public ParameterListShrinker(Predicate<List<T>> forAllFalsifier, Function<Integer, Arbitrary<T>> arbitraryProvider) {
		this.forAllFalsifier = forAllFalsifier;
		this.arbitraryProvider = arbitraryProvider;
	}

	public ShrinkResult<List<T>> shrinkListElements(List<T> originalParams, Optional<AssertionError> originalError, int originalDistance) {

		AssertionError[] lastFalsifiedError = { originalError.orElse(null) };
		List<T> lastFalsifiedParams = new ArrayList<>(originalParams);
		for (int i = 0; i < originalParams.size(); i++) {
			shrinkPosition(i, lastFalsifiedParams, lastFalsifiedError);
		}

		ShrinkableValue<List<T>> last = ShrinkableValue.of(lastFalsifiedParams, originalDistance);
		return ShrinkResult.of(last, lastFalsifiedError[0]);
	}

	private void shrinkPosition(int position, List<T> lastFalsifiedParams, AssertionError[] lastFalsifiedError) {
		T currentParam = lastFalsifiedParams.get(position);
		Predicate<T> elementFalsifier = createFalsifierForPosition(position, lastFalsifiedParams);
		Arbitrary<T> elementArbitrary = arbitraryProvider.apply(position);
		Shrinkable<T> shrinkTree = elementArbitrary.shrinkableFor(currentParam);
		Optional<ShrinkResult<T>> shrinkResults = shrinkTree.shrink(elementFalsifier);
		shrinkResults.ifPresent(shrinkResult -> {
			lastFalsifiedParams.set(position, shrinkResult.value());
			shrinkResult.error().ifPresent(ae -> lastFalsifiedError[0] = ae);
		});
	}

	private Predicate<T> createFalsifierForPosition(int position, List<T> lastFalsifiedParams) {
		return param -> {
			List<T> effectiveParams = new ArrayList<>(lastFalsifiedParams);
			effectiveParams.set(position, param);
			return forAllFalsifier.test(effectiveParams);
		};
	}

}
