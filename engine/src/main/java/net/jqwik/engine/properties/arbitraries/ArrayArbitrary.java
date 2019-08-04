package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;

public class ArrayArbitrary<A, T> extends MultivalueArbitraryBase<T> implements SizableArbitrary<A>, SelfConfiguringArbitrary<A> {

	private final Class<A> arrayClass;

	public ArrayArbitrary(Arbitrary<T> elementArbitrary, Class<A> arrayClass) {
		super(elementArbitrary);
		this.arrayClass = arrayClass;
	}

	@Override
	public RandomGenerator<A> generator(int genSize) {
		return createListGenerator(genSize).map(this::toArray);
	}

	@Override
	public Optional<ExhaustiveGenerator<A>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators
			.list(elementArbitrary, minSize, maxSize, maxNumberOfSamples)
			.map(generator -> generator.map(this::toArray));
	}

	@SuppressWarnings("unchecked")
	private A toArray(List<T> from) {
		A array = (A) Array.newInstance(arrayClass.getComponentType(), from.size());
		for (int i = 0; i < from.size(); i++) {
			Array.set(array, i, from.get(i));
		}
		return array;
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

	@Override
	public Arbitrary<A> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		elementArbitrary = configurator.configure(elementArbitrary, targetType);
		return configurator.configure(this, targetType);
	}
}
