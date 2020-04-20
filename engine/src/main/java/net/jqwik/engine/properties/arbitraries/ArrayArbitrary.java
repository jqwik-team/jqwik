package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class ArrayArbitrary<A, T> extends MultivalueArbitraryBase<T> implements StreamableArbitrary<T, A>, SelfConfiguringArbitrary<A> {

	private final Class<A> arrayClass;

	public ArrayArbitrary(Arbitrary<T> elementArbitrary, Class<A> arrayClass, boolean elementsUnique) {
		super(elementArbitrary, elementsUnique);
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

	@Override
	public EdgeCases<A> edgeCases() {
		return edgeCases(ShrinkableList::new).map(this::toArray);
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
	public <R> Arbitrary<R> reduce(R initial, BiFunction<R, T, R> accumulator) {
		// TODO: Remove duplication with DefaultCollectionArbitrary.reduce
		return this.map(streamable -> {
			// Couldn't find a way to use Stream.reduce since it requires a combinator
			@SuppressWarnings("unchecked")
			R[] result = (R[]) new Object[]{initial};
			@SuppressWarnings("unchecked")
			T[] array = (T[]) streamable;
			for (T each : array) {
				result[0] = accumulator.apply(result[0], each);
			}
			return result[0];
		});
	}

	@Override
	public StreamableArbitrary<T, A> ofMinSize(int minSize) {
		ArrayArbitrary<A, T> clone = typedClone();
		clone.minSize = minSize;
		return clone;
	}

	@Override
	public StreamableArbitrary<T, A> ofMaxSize(int maxSize) {
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
