package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;

import java.util.*;

public class ListArbitrary<T> extends DefaultCollectionArbitrary<T, List<T>> {

	public ListArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	public RandomGenerator<List<T>> generator(int genSize) {
		return listGenerator(genSize);
	}

}
