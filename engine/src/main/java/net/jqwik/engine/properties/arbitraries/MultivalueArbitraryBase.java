package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

abstract class MultivalueArbitraryBase<T> extends AbstractArbitraryBase {

	protected Arbitrary<T> elementArbitrary;
	protected int minSize = 0;
	protected int maxSize = RandomGenerators.DEFAULT_COLLECTION_SIZE;
	private final boolean elementsUnique;

	protected MultivalueArbitraryBase(Arbitrary<T> elementArbitrary, final boolean elementsUnique) {
		this.elementArbitrary = elementArbitrary;
		this.elementsUnique = elementsUnique;
	}

	protected RandomGenerator<List<T>> createListGenerator(int genSize) {
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, genSize);
		EdgeCases<List<T>> edgeCases = edgeCases(ShrinkableList::new);
		return RandomGenerators
				   .list(elementGenerator, minSize, maxSize, cutoffSize(genSize)) //
				   .withEdgeCases(genSize, edgeCases);
	}

	protected int cutoffSize(int genSize) {
		return RandomGenerators.defaultCutoffSize(minSize, maxSize, genSize);
	}

	protected RandomGenerator<T> elementGenerator(Arbitrary<T> elementArbitrary, int genSize) {
		return elementArbitrary.generator(genSize);
	}

	protected <C extends Collection<?>> EdgeCases<C> edgeCases(BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator) {
		EdgeCases<C> emptyListEdgeCase = (minSize == 0) ? emptyListEdgeCase(shrinkableCreator) : EdgeCases.none();
		EdgeCases<C> singleElementEdgeCases = (minSize <= 1 && maxSize >= 1) ? fixedSizeEdgeCases(1, shrinkableCreator) : EdgeCases.none();
		EdgeCases<C> fixedSizeEdgeCases = generateFixedSizeEdgeCases() ? fixedSizeEdgeCases(minSize, shrinkableCreator) : EdgeCases.none();
		return EdgeCases.concat(Arrays.asList(emptyListEdgeCase, singleElementEdgeCases, fixedSizeEdgeCases));
	}

	private boolean generateFixedSizeEdgeCases() {
		if (elementsUnique) {
			return false;
		}
		return minSize == maxSize && minSize > 1;
	}

	private <C extends Collection<?>> EdgeCases<C> fixedSizeEdgeCases(
		final int fixedSize,
		final BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator
	) {
		return elementArbitrary
				   .edgeCases()
				   .mapShrinkable((Shrinkable<T> shrinkableT) -> {
					   List<Shrinkable<T>> elements = new ArrayList<>(Collections.nCopies(fixedSize, shrinkableT));
					   return shrinkableCreator.apply(elements, minSize);
				   });
	}

	private <C extends Collection<?>> EdgeCases<C> emptyListEdgeCase(BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator) {
		return EdgeCases.fromSupplier(
			() -> shrinkableCreator.apply(Collections.emptyList(), minSize)
		);
	}

}
