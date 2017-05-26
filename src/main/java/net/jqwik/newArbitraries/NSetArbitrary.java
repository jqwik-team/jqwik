package net.jqwik.newArbitraries;

import java.util.*;

public class NSetArbitrary<T> extends NCollectionArbitrary<T, Set<T>> {

	public NSetArbitrary(NArbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0);
	}

	public NSetArbitrary(NArbitrary<T> elementArbitrary, int maxSize) {
		super(Set.class, elementArbitrary, maxSize);
	}

	@Override
	protected NShrinkableGenerator<Set<T>> baseGenerator(int tries) {
		return listGenerator(tries).map(HashSet::new);
	}
}
