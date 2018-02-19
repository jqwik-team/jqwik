package net.jqwik.properties.arbitraries;

import java.util.List;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.RandomGenerator;

public class ListArbitrary<T> extends DefaultCollectionArbitrary<T, List<T>> {

	public ListArbitrary(Arbitrary<T> elementArbitrary) {
		super(List.class, elementArbitrary, 0, 0);
	}

	@Override
	protected RandomGenerator<List<T>> baseGenerator(int tries) {
		return listGenerator(tries);
	}

}
