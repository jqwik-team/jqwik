package net.jqwik.properties.arbitraries;

import net.jqwik.properties.*;

import java.util.*;

public class SetArbitrary<T> extends CollectionArbitrary<T, Set<T>> {

	public SetArbitrary(Arbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0);
	}

	public SetArbitrary(Arbitrary<T> elementArbitrary, int maxSize) {
		super(Set.class, elementArbitrary, maxSize);
	}

	@Override
	protected RandomGenerator<Set<T>> baseGenerator(int tries) {
		return listGenerator(tries).map(HashSet::new);
	}
}
