package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.properties.*;

public class NListArbitrary<T> extends NCollectionArbitrary<T, List<T>> {

	public NListArbitrary(NArbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0);
	}

	public NListArbitrary(NArbitrary<T> elementArbitrary, int maxSize) {
		super(List.class, elementArbitrary, maxSize);
	}

	@Override
	protected NShrinkableGenerator<List<T>> baseGenerator(int tries) {
		return listGenerator(tries);
	}

}
