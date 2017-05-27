package net.jqwik.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class NArrayArbitrary<A, T> extends NNullableArbitrary<A> {

	private final NArbitrary<T> elementArbitrary;
	private int maxSize;


	public NArrayArbitrary(Class<A> arrayClass, NArbitrary<T> elementArbitrary) {
		this(arrayClass, elementArbitrary, 0);
	}

	public NArrayArbitrary(Class<A> arrayClass, NArbitrary<T> elementArbitrary, int maxSize) {
		super(arrayClass);
		this.elementArbitrary = elementArbitrary;
		this.maxSize = maxSize;
	}

	@Override
	protected NShrinkableGenerator<A> baseGenerator(int tries) {
		return listGenerator(tries).map(this::toArray);
	}

	private A toArray(List<T> from) {
		A array = (A) Array.newInstance(targetClass.getComponentType(), from.size());
		for (int i = 0; i < from.size(); i++) {
			Array.set(array, i, from.get(i));
		}
		return array;
	}
	protected NShrinkableGenerator<List<T>> listGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = NArbitrary.defaultMaxFromTries(tries);
		return createListGenerator(elementArbitrary, tries, effectiveMaxSize);
	}

	private <T> NShrinkableGenerator<List<T>> createListGenerator(NArbitrary<T> elementArbitrary, int tries, int maxSize) {
		int elementTries = Math.max(maxSize / 2, 1) * tries;
		NShrinkableGenerator<T> elementGenerator = elementArbitrary.generator(elementTries);
		return NShrinkableGenerators.list(elementGenerator, maxSize);
	}

	public void configure(MaxSize maxSize) {
		this.maxSize = maxSize.value();
	}


}
