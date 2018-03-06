package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

public class StreamArbitrary<T> extends DefaultCollectionArbitrary<T, Stream<T>> {

	public StreamArbitrary(Arbitrary<T> elementArbitrary) {
		super(Set.class, elementArbitrary);
	}

	@Override
	public RandomGenerator<Stream<T>> generator(int tries) {
		return listGenerator(tries).map(Collection::stream);
	}
}
