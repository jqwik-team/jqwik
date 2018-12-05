package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.exhaustive.*;

public class StreamArbitrary<T> extends DefaultCollectionArbitrary<T, Stream<T>> {

	public StreamArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	public RandomGenerator<Stream<T>> generator(int genSize) {
		return listGenerator(genSize).map(Collection::stream);
	}

	@Override
	public Optional<ExhaustiveGenerator<Stream<T>>> exhaustive() {
		return ExhaustiveGenerators.list(elementArbitrary, minSize, maxSize)
								   .map(generator -> generator.map(Collection::stream));
	}
}
