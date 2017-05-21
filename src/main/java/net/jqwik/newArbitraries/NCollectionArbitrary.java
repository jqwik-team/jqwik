package net.jqwik.newArbitraries;

import net.jqwik.api.*;

import java.util.*;

abstract class NCollectionArbitrary<T, U> extends NNullableArbitrary<U> {

	protected final NArbitrary<T> elementArbitrary;
	private int maxSize;

	public NCollectionArbitrary(Class<?> collectionClass, NArbitrary<T> elementArbitrary, int maxSize) {
		super(collectionClass);
		this.elementArbitrary = elementArbitrary;
		this.maxSize = maxSize;
	}

	protected NShrinkableGenerator<List<T>> listGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = NArbitrary.defaultCollectionSizeFromTries(tries);
		return createListGenerator(elementArbitrary, tries, effectiveMaxSize);
	}

	private NShrinkableGenerator<List<T>> createListGenerator(NArbitrary<T> elementArbitrary, int tries, int maxSize) {
		int elementTries = Math.max(maxSize / 2, 1) * tries;
		NShrinkableGenerator<T> elementGenerator = elementArbitrary.generator(elementTries);
		return NShrinkableGenerators.list(elementGenerator, maxSize);
	}

	public void configure(MaxSize maxSize) {
		this.maxSize = maxSize.value();
	}

}
