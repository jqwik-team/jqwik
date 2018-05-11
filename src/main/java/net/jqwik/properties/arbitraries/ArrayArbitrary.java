package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class ArrayArbitrary<A, T> extends AbstractArbitraryBase implements SizableArbitrary<A> {

	private static final int DEFAULT_MAX_SIZE = 255;

	private final Class<A> arrayClass;
	private final Arbitrary<T> elementArbitrary;
	private int maxSize = DEFAULT_MAX_SIZE;
	private int minSize = 0;

	public ArrayArbitrary(Class<A> arrayClass, Arbitrary<T> elementArbitrary) {
		this.arrayClass = arrayClass;
		this.elementArbitrary = elementArbitrary;
	}

	@Override
	public RandomGenerator<A> generator(int genSize) {
		return createListGenerator(genSize).map(this::toArray);
	}

	@SuppressWarnings("unchecked")
	private A toArray(List<T> from) {
		A array = (A) Array.newInstance(arrayClass.getComponentType(), from.size());
		for (int i = 0; i < from.size(); i++) {
			Array.set(array, i, from.get(i));
		}
		return array;
	}

	private RandomGenerator<List<T>> createListGenerator(int genSize) {
		int cutoffSize = RandomGenerators.defaultCutoffSize(minSize, maxSize, genSize);
		RandomGenerator<T> elementGenerator = elementArbitrary.generator(genSize);
		List<T> emptyList = Collections.emptyList();
		List<Shrinkable<List<T>>> edgeCases = Stream.of(emptyList).filter(l -> l.size() >= minSize)
													.filter(l -> maxSize == 0 || l.size() <= maxSize).map(Shrinkable::unshrinkable).collect(Collectors.toList());
		return RandomGenerators.list(elementGenerator, minSize, maxSize, cutoffSize).withEdgeCases(genSize, edgeCases);
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
