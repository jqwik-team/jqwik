package net.jqwik.engine.properties.arbitraries;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class DefaultArrayArbitrary<T, A> extends MultivalueArbitraryBase<T, A> implements ArrayArbitrary<T, A>, SelfConfiguringArbitrary<A> {

	private final Class<A> arrayClass;

	public DefaultArrayArbitrary(Arbitrary<T> elementArbitrary, Class<A> arrayClass) {
		super(elementArbitrary);
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
	public ArrayArbitrary<T, A> withSizeDistribution(RandomDistribution distribution) {
		return (ArrayArbitrary<T, A>) super.withSizeDistribution(distribution);
	}

	@Override
	public RandomGenerator<A> generator(int genSize) {
		return createListGenerator(genSize, false).map(this::toArray);
	}

	@Override
	public RandomGenerator<A> generatorWithEmbeddedEdgeCases(int genSize) {
		return createListGenerator(genSize, true).map(this::toArray);
	}

	@Override
	public Optional<ExhaustiveGenerator<A>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators
					   .list(elementArbitrary, minSize, maxSize, uniquenessExtractors, maxNumberOfSamples)
					   .map(generator -> generator.map(this::toArray));
	}

	@Override
	public EdgeCases<A> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.map(
				edgeCases(
						(elements, minSize1) -> new ShrinkableList<>(elements, minSize1, maxSize, uniquenessExtractors),
						maxEdgeCases
				),
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

	@SuppressWarnings("unchecked")
	@Override
	protected Iterable<T> toIterable(A array) {
		return () -> Arrays.stream((T[]) array).iterator();
	}

	@Override
	public Arbitrary<A> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		targetType.getComponentType().ifPresent(elementType -> elementArbitrary = configurator.configure(elementArbitrary, elementType));
		return configurator.configure(this, targetType);
	}

	@Override
	public ArrayArbitrary<T, A> uniqueElements() {
		return (ArrayArbitrary<T, A>) uniqueElements(FeatureExtractor.identity());
	}

	@Override
	public ArrayArbitrary<T, A> uniqueElements(Function<T, Object> by) {
		FeatureExtractor<T> featureExtractor = by::apply;
		return (ArrayArbitrary<T, A>) super.uniqueElements(featureExtractor);
	}
}
