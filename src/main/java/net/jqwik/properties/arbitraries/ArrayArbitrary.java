package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class ArrayArbitrary<A, T> extends NullableArbitraryBase<A> implements SizableArbitrary<A> {

	private final Arbitrary<T> elementArbitrary;
	private int maxSize = 0;
	private int minSize = 0;

	public ArrayArbitrary(Class<A> arrayClass, Arbitrary<T> elementArbitrary) {
		super(arrayClass);
		this.elementArbitrary = elementArbitrary;
	}

	@Override
	protected RandomGenerator<A> baseGenerator(int tries) {
		return listGenerator(tries).map(this::toArray);
	}

	@SuppressWarnings("unchecked")
	private A toArray(List<T> from) {
		A array = (A) Array.newInstance(getTargetClass().getComponentType(), from.size());
		for (int i = 0; i < from.size(); i++) {
			Array.set(array, i, from.get(i));
		}
		return array;
	}

	private RandomGenerator<List<T>> listGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = Arbitrary.defaultMaxCollectionSizeFromTries(tries);
		return createListGenerator(elementArbitrary, tries, effectiveMaxSize);
	}

	private RandomGenerator<List<T>> createListGenerator(Arbitrary<T> elementArbitrary, int tries, int maxSize) {
		int elementTries = Math.max(maxSize / 2, 1) * tries;
		RandomGenerator<T> elementGenerator = elementArbitrary.generator(elementTries);
		List<T> emptyList = Collections.emptyList();
		List<Shrinkable<List<T>>> samples = Stream.of(emptyList).filter(l -> l.size() >= minSize)
				.filter(l -> maxSize == 0 || l.size() <= maxSize).map(Shrinkable::unshrinkable).collect(Collectors.toList());
		return RandomGenerators.list(elementGenerator, minSize, maxSize).withShrinkableSamples(samples);
	}

	@Override
	public SizableArbitrary<A> ofMinSize(int minSize) {
		ArrayArbitrary<A, T> clone = typedClone();
		clone.minSize = minSize;
		return clone;
	}

	@Override
	public SizableArbitrary<A> ofMaxSize(int maxSize) {
		ArrayArbitrary<A, T> clone = typedClone();
		clone.maxSize = maxSize;
		return clone;
	}
}
