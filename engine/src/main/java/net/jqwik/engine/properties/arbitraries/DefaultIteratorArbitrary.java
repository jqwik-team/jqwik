package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class DefaultIteratorArbitrary<T> extends MultivalueArbitraryBase<T, Iterator<T>> implements IteratorArbitrary<T> {

	public DefaultIteratorArbitrary(Arbitrary<T> elementArbitrary, boolean elementsUnique) {
		super(elementArbitrary, elementsUnique);
	}

	@Override
	protected Iterable<T> toIterable(Iterator<T> streamable) {
		return () -> streamable;
	}

	@Override
	public RandomGenerator<Iterator<T>> generator(int genSize) {
		return createListGenerator(genSize).map(List::iterator);
	}

	@Override
	public Optional<ExhaustiveGenerator<Iterator<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.list(elementArbitrary, minSize, maxSize, maxNumberOfSamples)
								   .map(generator -> generator.map(List::iterator));
	}

	@Override
	public EdgeCases<Iterator<T>> edgeCases() {
		return edgeCases((elements, minSize1) -> new ShrinkableList<>(elements, minSize1, maxSize)).map(List::iterator);
	}

	@Override
	public IteratorArbitrary<T> ofMaxSize(int maxSize) {
		return (IteratorArbitrary<T>) super.ofMaxSize(maxSize);
	}

	@Override
	public IteratorArbitrary<T> ofMinSize(int minSize) {
		return (IteratorArbitrary<T>) super.ofMinSize(minSize);
	}

}
