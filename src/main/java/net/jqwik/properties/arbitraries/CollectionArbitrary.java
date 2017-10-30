package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.constraints.Size;
import net.jqwik.properties.*;

abstract class CollectionArbitrary<T, U> extends NullableArbitrary<U> {

	protected final Arbitrary<T> elementArbitrary;
	private int minSize;
	private int maxSize;

	protected CollectionArbitrary(Class<?> collectionClass, Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		super(collectionClass);
		this.elementArbitrary = elementArbitrary;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	protected RandomGenerator<List<T>> listGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = Arbitrary.defaultCollectionSizeFromTries(tries);
		return createListGenerator(elementArbitrary, tries, effectiveMaxSize);
	}

	private RandomGenerator<List<T>> createListGenerator(Arbitrary<T> elementArbitrary, int tries, int effectiveMaxSize) {
		int elementTries = Math.max(effectiveMaxSize / 2, 1) * tries;
		RandomGenerator<T> elementGenerator = elementArbitrary.generator(elementTries);
		List<T> emptyList = Collections.emptyList();
		List<Shrinkable<List<T>>> samples = Stream.of(emptyList)
			.filter(l -> l.size() >= minSize && l.size() <= effectiveMaxSize)
			.map(Shrinkable::unshrinkable)
			.collect(Collectors.toList());

		return RandomGenerators.list(elementGenerator, minSize, effectiveMaxSize).withShrinkableSamples(samples);
	}

	public void configure(Size size) {
		this.maxSize = size.max();
		this.minSize = size.min();
	}

}
