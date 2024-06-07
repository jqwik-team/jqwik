package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

import org.jspecify.annotations.*;

public class DefaultIteratorArbitrary<T extends @Nullable Object> extends MultivalueArbitraryBase<T, Iterator<T>> implements IteratorArbitrary<T> {

	public DefaultIteratorArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	protected Iterable<T> toIterable(Iterator<T> streamable) {
		return () -> streamable;
	}

	@Override
	public RandomGenerator<Iterator<T>> generator(int genSize) {
		return createListGenerator(genSize, false).map(List::iterator);
	}

	@Override
	public RandomGenerator<Iterator<T>> generatorWithEmbeddedEdgeCases(int genSize) {
		return createListGenerator(genSize, true).map(List::iterator);
	}

	@Override
	public Optional<ExhaustiveGenerator<Iterator<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.list(elementArbitrary, minSize, maxSize(), uniquenessExtractors, maxNumberOfSamples)
								   .map(generator -> generator.map(List::iterator));
	}

	@Override
	public EdgeCases<Iterator<T>> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.map(
				edgeCases((elements, minimalSize) -> new ShrinkableList<>(elements, minimalSize, maxSize(), uniquenessExtractors, elementArbitrary), maxEdgeCases),
				List::iterator
		);
	}

	@Override
	public IteratorArbitrary<T> ofMaxSize(int maxSize) {
		return (IteratorArbitrary<T>) super.ofMaxSize(maxSize);
	}

	@Override
	public IteratorArbitrary<T> ofMinSize(int minSize) {
		return (IteratorArbitrary<T>) super.ofMinSize(minSize);
	}

	@Override
	public IteratorArbitrary<T> withSizeDistribution(RandomDistribution distribution) {
		return (IteratorArbitrary<T>) super.withSizeDistribution(distribution);
	}

	@Override
	public IteratorArbitrary<T> uniqueElements(Function<? super T, ?> by) {
		FeatureExtractor<T> featureExtractor = by::apply;
		return (IteratorArbitrary<T>) super.uniqueElements(featureExtractor);
	}

	@Override
	public IteratorArbitrary<T> uniqueElements() {
		return (IteratorArbitrary<T>) super.uniqueElements();
	}

}
