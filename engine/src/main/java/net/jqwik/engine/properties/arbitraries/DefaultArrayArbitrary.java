package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class DefaultArrayArbitrary<T, A> extends MultivalueArbitraryBase<T, A> implements ArrayArbitrary<T, A>, SelfConfiguringArbitrary<A> {

	private final Class<A> arrayClass;

	public DefaultArrayArbitrary(Arbitrary<T> elementArbitrary, Class<A> arrayClass, boolean elementsUnique) {
		super(elementArbitrary, elementsUnique);
		this.arrayClass = arrayClass;
	}

	@Override
	public ArrayArbitrary<T, A> ofMinSize(int minSize) {
		return (ArrayArbitrary<T, A>) super.ofMinSize(minSize);
	}

	@Override
	public ArrayArbitrary<T, A> ofMaxSize(int maxSize) {
		return (ArrayArbitrary<T, A>) super.ofMaxSize(maxSize);
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
		return EdgeCasesSupport.map(
				edgeCases((elements, minSize1) -> new ShrinkableList<>(elements, minSize1, maxSize)),
				this::toArray
		);
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
	protected Iterable<T> toIterable(A array) {
		//noinspection unchecked
		return () -> Arrays.stream((T[]) array).iterator();
	}

	@Override
	public Arbitrary<A> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		targetType.getComponentType().ifPresent(elementType -> {
			elementArbitrary = configurator.configure(elementArbitrary, elementType);
		});
		return configurator.configure(this, targetType);
	}

}
