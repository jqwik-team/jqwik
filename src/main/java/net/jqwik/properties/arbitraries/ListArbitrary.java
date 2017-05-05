package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.properties.*;
import net.jqwik.properties.shrinking.*;

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

	@Override
	public Shrinkable<List<T>> shrinkableFor(List<T> value) {
		return new ListShrinker<T>(elementArbitrary).shrink(value);
	}
}
