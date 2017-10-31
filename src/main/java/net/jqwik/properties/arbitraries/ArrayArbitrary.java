package net.jqwik.properties.arbitraries;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.constraints.Size;
import net.jqwik.properties.*;

public class ArrayArbitrary<A, T> extends NullableArbitrary<A> {

	private final Arbitrary<T> elementArbitrary;
	private int maxSize;
	private int minSize;


	public ArrayArbitrary(Class<A> arrayClass, Arbitrary<T> elementArbitrary) {
		this(arrayClass, elementArbitrary, 0,0);
	}

	public ArrayArbitrary(Class<A> arrayClass, Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		super(arrayClass);
		this.elementArbitrary = elementArbitrary;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	@Override
	protected RandomGenerator<A> baseGenerator(int tries) {
		return listGenerator(tries).map(this::toArray);
	}

	@SuppressWarnings("unchecked")
	private A toArray(List<T> from) {
		A array = (A) Array.newInstance(targetClass.getComponentType(), from.size());
		for (int i = 0; i < from.size(); i++) {
			Array.set(array, i, from.get(i));
		}
		return array;
	}

	private RandomGenerator<List<T>> listGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = Arbitrary.defaultCollectionSizeFromTries(tries);
		return createListGenerator(elementArbitrary, tries, effectiveMaxSize);
	}

	private RandomGenerator<List<T>> createListGenerator(Arbitrary<T> elementArbitrary, int tries, int maxSize) {
		int elementTries = Math.max(maxSize / 2, 1) * tries;
		RandomGenerator<T> elementGenerator = elementArbitrary.generator(elementTries);
		List<T> emptyList = Collections.emptyList();
		List<Shrinkable<List<T>>> samples = Stream.of(emptyList)
			.filter(l -> l.size() >= minSize)
			.filter(l -> maxSize == 0 || l.size() <= maxSize)
			.map(Shrinkable::unshrinkable)
			.collect(Collectors.toList());
		return RandomGenerators.list(elementGenerator, minSize, maxSize).withShrinkableSamples(samples);
	}

	public void configure(Size size) {
		this.maxSize = size.max();
		this.minSize = size.min();
	}


}
