package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

abstract class MultivalueArbitraryBase<T> extends AbstractArbitraryBase {

	protected Arbitrary<T> elementArbitrary;
	protected int minSize = 0;
	protected int maxSize = RandomGenerators.DEFAULT_COLLECTION_SIZE;

	protected MultivalueArbitraryBase(Arbitrary<T> elementArbitrary) {
		this.elementArbitrary = elementArbitrary;
	}

	protected RandomGenerator<List<T>> createListGenerator(int genSize) {
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, genSize);
		List<Shrinkable<List<T>>> samples = edgeCases(new ArrayList<>());
		return RandomGenerators
				   .list(elementGenerator, minSize, maxSize, cutoffSize(genSize)) //
				   .withEdgeCases(genSize, samples);
	}

	protected int cutoffSize(int genSize) {
		return RandomGenerators.defaultCutoffSize(minSize, maxSize, genSize);
	}

	protected <C extends Collection> List<Shrinkable<C>> edgeCases(C sample) {
		return Stream.of(sample)
					 .filter(l -> l.size() >= minSize)
					 .filter(l -> maxSize == 0 || l.size() <= maxSize)
					 .map(Shrinkable::unshrinkable)
					 .collect(Collectors.toList());
	}

	protected RandomGenerator<T> elementGenerator(Arbitrary<T> elementArbitrary, int genSize) {
		return elementArbitrary.generator(genSize);
	}

}
