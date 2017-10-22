package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

abstract class CollectionArbitrary<T, U> extends NullableArbitrary<U> {

	protected final Arbitrary<T> elementArbitrary;
	private int maxSize;

	public CollectionArbitrary(Class<?> collectionClass, Arbitrary<T> elementArbitrary, int maxSize) {
		super(collectionClass);
		this.elementArbitrary = elementArbitrary;
		this.maxSize = maxSize;
	}

	protected RandomGenerator<List<T>> listGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = Arbitrary.defaultCollectionSizeFromTries(tries);
		return createListGenerator(elementArbitrary, tries, effectiveMaxSize);
	}

	private RandomGenerator<List<T>> createListGenerator(Arbitrary<T> elementArbitrary, int tries, int maxSize) {
		int elementTries = Math.max(maxSize / 2, 1) * tries;
		RandomGenerator<T> elementGenerator = elementArbitrary.generator(elementTries);
		return RandomGenerators.list(elementGenerator, maxSize).withShrinkableSamples(Collections.emptyList());
	}

	public void configure(MaxSize maxSize) {
		this.maxSize = maxSize.value();
	}

}
