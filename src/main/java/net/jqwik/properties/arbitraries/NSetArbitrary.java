package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.properties.*;

public class NSetArbitrary<T> extends NCollectionArbitrary<T, Set<T>> {

	public NSetArbitrary(Arbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0);
	}

	public NSetArbitrary(Arbitrary<T> elementArbitrary, int maxSize) {
		super(Set.class, elementArbitrary, maxSize);
	}

	@Override
	protected RandomGenerator<Set<T>> baseGenerator(int tries) {
		return listGenerator(tries).map(HashSet::new);
	}
}
