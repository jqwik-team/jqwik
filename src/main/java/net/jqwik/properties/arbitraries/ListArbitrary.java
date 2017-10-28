package net.jqwik.properties.arbitraries;

import java.util.List;

import net.jqwik.api.Arbitrary;
import net.jqwik.properties.RandomGenerator;

public class ListArbitrary<T> extends CollectionArbitrary<T, List<T>> {

	public ListArbitrary(Arbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0);
	}

	public ListArbitrary(Arbitrary<T> elementArbitrary, int maxSize) {
		super(List.class, elementArbitrary, maxSize);
	}

	@Override
	protected RandomGenerator<List<T>> baseGenerator(int tries) {
		return listGenerator(tries);
	}

}
