package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.util.*;

public class SetArbitrary<T> extends DefaultCollectionArbitrary<T, Set<T>> {

	public SetArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	public RandomGenerator<Set<T>> generator(int genSize) {
		int cutoffSize = cutoffSize(genSize);
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, genSize);
		List<Shrinkable<Set<T>>> samples = samplesList(new HashSet<>());
		return RandomGenerators.set(elementGenerator, minSize, maxSize, cutoffSize).withEdgeCases(samples);
	}
}
