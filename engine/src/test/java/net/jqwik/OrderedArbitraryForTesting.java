package net.jqwik;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class OrderedArbitraryForTesting<T> implements Arbitrary<T> {
	private final T[] elements;

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
		List<Shrinkable<T>> shrinkables =
			Arrays.stream(elements)
				  .map(Shrinkable::unshrinkable)
			.collect(Collectors.toList());
		return EdgeCases.fromShrinkables(shrinkables);
	}
}
