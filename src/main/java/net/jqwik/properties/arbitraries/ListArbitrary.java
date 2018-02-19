package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.util.*;

public class ListArbitrary<T> extends DefaultCollectionArbitrary<T, List<T>> {

	public ListArbitrary(Arbitrary<T> elementArbitrary) {
		super(List.class, elementArbitrary);
	}

	@Override
	protected RandomGenerator<List<T>> baseGenerator(int tries) {
		return listGenerator(tries);
	}

}
