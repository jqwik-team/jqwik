package net.jqwik.properties.arbitraries;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.properties.arbitraries.exhaustive.*;
import net.jqwik.properties.arbitraries.randomized.*;

public class ArrayArbitrary<A, T> extends AbstractArbitraryBase implements SizableArbitrary<A>, SelfConfiguringArbitrary<A> {

	private final Class<A> arrayClass;
	private Arbitrary<T> elementArbitrary;
	private int maxSize = RandomGenerators.DEFAULT_COLLECTION_SIZE;
	private int minSize = 0;

	public ArrayArbitrary(Class<A> arrayClass, Arbitrary<T> elementArbitrary) {
		this.arrayClass = arrayClass;
		this.elementArbitrary = elementArbitrary;
	}

	@Override
	public RandomGenerator<A> generator(int genSize) {
		return createListGenerator(genSize).map(this::toArray);
	}

	@Override
	public Optional<ExhaustiveGenerator<A>> exhaustive() {
		return ExhaustiveGenerators.list(elementArbitrary, minSize, maxSize).map(generator -> generator.map(this::toArray));
	}

	@SuppressWarnings("unchecked")
	private A toArray(List<T> from) {
		A array = (A) Array.newInstance(arrayClass.getComponentType(), from.size());
		for (int i = 0; i < from.size(); i++) {
			Array.set(array, i, from.get(i));
		}
		return array;
	}

	// TODO: Remove duplication with DefaultCollectionArbitrary.listGenerator(genSize)
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

	@Override
	public Arbitrary<A> configure(ArbitraryConfigurator configurator, List<Annotation> annotations) {
		elementArbitrary = configurator.configure(elementArbitrary, annotations);
		return configurator.configure(this, annotations);
	}
}
