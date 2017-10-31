package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.constraints.Size;
import net.jqwik.properties.*;

abstract class CollectionArbitrary<T, U> extends NullableArbitrary<U> {

	protected final Arbitrary<T> elementArbitrary;
	protected int minSize;
	protected int maxSize;

	protected CollectionArbitrary(Class<?> collectionClass, Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		super(collectionClass);
		this.elementArbitrary = elementArbitrary;
		this.minSize = minSize;
		this.maxSize = maxSize;
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
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, tries, effectiveMaxSize);
		List<Shrinkable<List<T>>> samples = samplesList(effectiveMaxSize, Collections.emptyList());
		return RandomGenerators.list(elementGenerator, minSize, effectiveMaxSize).withShrinkableSamples(samples);
	}

	protected <C extends Collection> List<Shrinkable<C>> samplesList(int effectiveMaxSize, C sample) {
		return Stream.of(sample)
			.filter(l -> l.size() >= minSize)
			.filter(l -> maxSize == 0 || l.size() <= maxSize)
			.map(Shrinkable::unshrinkable)
			.collect(Collectors.toList());
	}

	protected RandomGenerator<T> elementGenerator(Arbitrary<T> elementArbitrary, int tries, int effectiveMaxSize) {
		int elementTries = Math.max(effectiveMaxSize / 2, 1) * tries;
		return elementArbitrary.generator(elementTries);
	}

	public void configure(Size size) {
		this.maxSize = size.max();
		this.minSize = size.min();
	}

}
