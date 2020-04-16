package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
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
		List<Shrinkable<List<T>>> samples = deprecatedEdgeCases(new ArrayList<>());
		return RandomGenerators
				   .list(elementGenerator, minSize, maxSize, cutoffSize(genSize)) //
				   .withEdgeCases(genSize, samples);
	}

	protected int cutoffSize(int genSize) {
		return RandomGenerators.defaultCutoffSize(minSize, maxSize, genSize);
	}

	protected <C extends Collection<?>> List<Shrinkable<C>> deprecatedEdgeCases(C sample) {
		return Stream.of(sample)
					 .filter(l -> l.size() >= minSize)
					 .filter(l -> maxSize == 0 || l.size() <= maxSize)
					 .map(Shrinkable::unshrinkable)
					 .collect(Collectors.toList());
	}

	protected RandomGenerator<T> elementGenerator(Arbitrary<T> elementArbitrary, int genSize) {
		return elementArbitrary.generator(genSize);
	}

	protected <C extends Collection<?>> EdgeCases<C> edgeCases(BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator) {
		EdgeCases<C> emptyEdgeCases = (minSize == 0) ? emptyListEdgeCase(shrinkableCreator) : EdgeCases.none();
		EdgeCases<C> edgeCasesWithElements = (minSize <= 1) ? singleElementEdgeCases(shrinkableCreator) : EdgeCases.none();
		return EdgeCases.concat(emptyEdgeCases, edgeCasesWithElements);
	}

	private <C extends Collection<?>> EdgeCases<C> singleElementEdgeCases(BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator) {
		return elementArbitrary
				   .edgeCases()
				   .mapShrinkable((Shrinkable<T> shrinkableT) -> {
					   List<Shrinkable<T>> elements = Collections.singletonList(shrinkableT);
					   return shrinkableCreator.apply(elements, minSize);
				   });
	}

	private <C extends Collection<?>> EdgeCases<C> emptyListEdgeCase(BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator) {
		return EdgeCases.fromSupplier(
			() -> shrinkableCreator.apply(Collections.emptyList(), minSize)
		);
	}

}
