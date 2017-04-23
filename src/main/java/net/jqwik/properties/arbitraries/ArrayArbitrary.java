package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.lang.reflect.*;
import java.util.*;

public class ArrayArbitrary<A, T> extends NullableArbitrary<A> {

	private final Arbitrary<T> elementArbitrary;
	private int maxSize;


	public ArrayArbitrary(Class<A> arrayClass, Arbitrary<T> elementArbitrary) {
		this(arrayClass, elementArbitrary, 0);
	}

	public ArrayArbitrary(Class<A> arrayClass, Arbitrary<T> elementArbitrary, int maxSize) {
		super(arrayClass);
		this.elementArbitrary = elementArbitrary;
		this.maxSize = maxSize;
	}

	@Override
	protected RandomGenerator<A> baseGenerator(int tries) {
		return listGenerator(tries).map(this::toArray);
	}

	private A toArray(List<T> from) {
		A array = (A) java.lang.reflect.Array.newInstance(targetClass.getComponentType(), from.size());
		for (int i = 0; i < from.size(); i++) {
			Array.set(array, i, from.get(i));
		}
		return array;
	}
	protected RandomGenerator<List<T>> listGenerator(int tries) {
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
