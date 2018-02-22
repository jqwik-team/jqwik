package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.util.*;
import java.util.stream.*;

abstract class DefaultCollectionArbitrary<T, U> extends NullableArbitraryBase<U> implements SizableArbitrary<U> {

	protected final Arbitrary<T> elementArbitrary;
	protected int minSize = 0;
	protected int maxSize = 0;

	protected DefaultCollectionArbitrary(Class<?> collectionClass, Arbitrary<T> elementArbitrary) {
		super(collectionClass);
		this.elementArbitrary = elementArbitrary;
	}

	protected RandomGenerator<List<T>> listGenerator(int tries) {
		int effectiveMaxSize = effectiveMaxSize(tries);
		return createListGenerator(elementArbitrary, tries, effectiveMaxSize);
	}

	protected int effectiveMaxSize(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = Arbitrary.defaultCollectionSizeFromTries(tries);
		return effectiveMaxSize;
	}

	private RandomGenerator<List<T>> createListGenerator(Arbitrary<T> elementArbitrary, int tries, int effectiveMaxSize) {
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, tries);
		List<Shrinkable<List<T>>> samples = samplesList(effectiveMaxSize, new ArrayList<>());
		return RandomGenerators.list(elementGenerator, minSize, effectiveMaxSize).withShrinkableSamples(samples);
	}

	protected <C extends Collection> List<Shrinkable<C>> samplesList(int effectiveMaxSize, C sample) {
		return Stream.of(sample).filter(l -> l.size() >= minSize).filter(l -> maxSize == 0 || l.size() <= maxSize)
				.map(Shrinkable::unshrinkable).collect(Collectors.toList());
	}

	protected RandomGenerator<T> elementGenerator(Arbitrary<T> elementArbitrary, int tries) {
		// Stepping down into element generators will half the number of tries, but never go below 10
		int elementTries = Math.max(tries / 2, 10);
		return elementArbitrary.generator(elementTries);
	}

	@Override
	public SizableArbitrary<U> ofMinSize(int minSize) {
		DefaultCollectionArbitrary<T, U> clone = typedClone();
		clone.minSize = minSize;
		return clone;
	}

	@Override
	public SizableArbitrary<U> ofMaxSize(int maxSize) {
		DefaultCollectionArbitrary<T, U> clone = typedClone();
		clone.maxSize = maxSize;
		return clone;
	}
}
