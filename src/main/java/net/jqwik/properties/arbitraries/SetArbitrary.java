package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.Arbitrary;
import net.jqwik.properties.*;

public class SetArbitrary<T> extends CollectionArbitrary<T, Set<T>> {

	public SetArbitrary(Arbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0, 0);
	}

	public SetArbitrary(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		super(Set.class, elementArbitrary, minSize, maxSize);
	}

	@Override
	protected RandomGenerator<Set<T>> baseGenerator(int tries) {
		int effectiveMaxSize = effectiveMaxSize(tries);
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, tries, effectiveMaxSize);
		List<Shrinkable<Set<T>>> samples = samplesList(effectiveMaxSize, Collections.emptySet());
		return RandomGenerators.set(elementGenerator, minSize, effectiveMaxSize).withShrinkableSamples(samples);
	}
}
