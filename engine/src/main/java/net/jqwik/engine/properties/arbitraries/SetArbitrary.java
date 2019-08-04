package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class SetArbitrary<T> extends DefaultCollectionArbitrary<T, Set<T>> {

	public SetArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	public RandomGenerator<Set<T>> generator(int genSize) {
		int cutoffSize = cutoffSize(genSize);
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, genSize);
		List<Shrinkable<Set<T>>> samples = edgeCases(new HashSet<>());
		return RandomGenerators.set(elementGenerator, minSize, maxSize, cutoffSize).withEdgeCases(genSize, samples);
	}

	@Override
	public Optional<ExhaustiveGenerator<Set<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.set(elementArbitrary, minSize, maxSize, maxNumberOfSamples);

	}
}
