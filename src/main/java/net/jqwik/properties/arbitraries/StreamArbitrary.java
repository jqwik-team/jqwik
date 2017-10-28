package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.Stream;

import net.jqwik.api.Arbitrary;
import net.jqwik.properties.RandomGenerator;

public class StreamArbitrary<T> extends CollectionArbitrary<T, Stream<T>> {

	public StreamArbitrary(Arbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0);
	}

	public StreamArbitrary(Arbitrary<T> elementArbitrary, int maxSize) {
		super(Set.class, elementArbitrary, maxSize);
	}

	@Override
	protected RandomGenerator<Stream<T>> baseGenerator(int tries) {
		return listGenerator(tries).map(Collection::stream);
	}
}
