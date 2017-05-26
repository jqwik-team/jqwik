package net.jqwik.newArbitraries;

import java.util.*;
import java.util.stream.*;

public class NStreamArbitrary<T> extends NCollectionArbitrary<T, Stream<T>> {

	public NStreamArbitrary(NArbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0);
	}

	public NStreamArbitrary(NArbitrary<T> elementArbitrary, int maxSize) {
		super(Set.class, elementArbitrary, maxSize);
	}

	@Override
	protected NShrinkableGenerator<Stream<T>> baseGenerator(int tries) {
		return listGenerator(tries).map(Collection::stream);
	}
}
