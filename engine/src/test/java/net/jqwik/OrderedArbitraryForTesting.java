package net.jqwik;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class OrderedArbitraryForTesting<T> implements Arbitrary<T> {
	private final T[] elements;

	public static Arbitrary<Integer> between(int min, int max) {
		List<Integer> values = new ArrayList<>();
		for (int i = min; i <= max ; i++) {
			values.add(i);
		}
		return new OrderedArbitraryForTesting<>(values.toArray(new Integer[0]));
	}

	@SafeVarargs
	public OrderedArbitraryForTesting(T ... elements) {
		this.elements = elements;
	}

	@Override
	public RandomGenerator<T> generator(final int genSize) {
		return RandomGenerators.samples(elements);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(final long maxNumberOfSamples) {
			return ExhaustiveGenerators.choose(Arrays.asList(elements), maxNumberOfSamples);
	}

	@Override
	public EdgeCases<T> edgeCases() {
		return EdgeCases.none();
	}
}
