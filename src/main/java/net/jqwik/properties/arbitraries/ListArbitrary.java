package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class ListArbitrary<T> extends NullableArbitrary<List<T>> {

	private final Arbitrary<T> elementArbitrary;
	private int maxSize;

	public ListArbitrary(Arbitrary<T> elementArbitrary) {
		this(elementArbitrary, 0);
	}

	public ListArbitrary(Arbitrary<T> elementArbitrary, int maxSize) {
		super(List.class);
		this.elementArbitrary = elementArbitrary;
		this.maxSize = maxSize;
	}

	@Override
	protected RandomGenerator<List<T>> baseGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = Arbitrary.defaultMaxFromTries(tries);
		return createListGenerator(elementArbitrary, tries, effectiveMaxSize);
	}

	private <T> RandomGenerator<List<T>> createListGenerator(Arbitrary<T> elementArbitrary, int tries, int maxSize) {
		int elementTries = Math.max(maxSize / 2, 1) * tries;
		RandomGenerator<T> elementGenerator = elementArbitrary.generator(elementTries);
		return RandomGenerators.list(elementGenerator, maxSize);
	}

	public void configure(MaxSize maxSize) {
		this.maxSize = maxSize.value();
	}

}
