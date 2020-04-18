package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class StreamArbitrary<T> extends DefaultCollectionArbitrary<T, Stream<T>> {

	public StreamArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	protected Iterable<T> toIterable(Stream<T> streamable) {
		return streamable::iterator;
	}

	@Override
	public RandomGenerator<Stream<T>> generator(int genSize) {
		return createListGenerator(genSize).map(Collection::stream);
	}

	@Override
	public Optional<ExhaustiveGenerator<Stream<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators
				   .list(elementArbitrary, minSize, maxSize, maxNumberOfSamples)
				   .map(generator -> generator.map(Collection::stream));
	}

	@Override
	public EdgeCases<Stream<T>> edgeCases() {
		return edgeCases(ShrinkableList::new).map(Collection::stream);
	}

}
