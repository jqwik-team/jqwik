package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.Arbitrary;
import net.jqwik.properties.RandomGenerator;

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
