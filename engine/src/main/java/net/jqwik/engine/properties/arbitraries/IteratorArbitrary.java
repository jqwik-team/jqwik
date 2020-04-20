package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class IteratorArbitrary<T> extends DefaultCollectionArbitrary<T, Iterator<T>> {

	public IteratorArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary, false);
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
		return edgeCases(ShrinkableList::new).map(List::iterator);
	}
}
