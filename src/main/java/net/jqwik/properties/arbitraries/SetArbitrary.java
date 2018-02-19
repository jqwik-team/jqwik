package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.util.*;

public class SetArbitrary<T> extends DefaultCollectionArbitrary<T, Set<T>> {

	public SetArbitrary(Arbitrary<T> elementArbitrary) {
		super(Set.class, elementArbitrary);
	}

	@Override
	protected RandomGenerator<Set<T>> baseGenerator(int tries) {
		int effectiveMaxSize = effectiveMaxSize(tries);
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, tries);
		List<Shrinkable<Set<T>>> samples = samplesList(effectiveMaxSize, new HashSet<>());
		return RandomGenerators.set(elementGenerator, minSize, effectiveMaxSize).withShrinkableSamples(samples);
	}
}
