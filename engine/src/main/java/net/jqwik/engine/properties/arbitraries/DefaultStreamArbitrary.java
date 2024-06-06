package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class DefaultStreamArbitrary<T> extends MultivalueArbitraryBase<T, Stream<T>> implements StreamArbitrary<T> {

	public DefaultStreamArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	protected Iterable<T> toIterable(Stream<T> streamable) {
		return streamable::iterator;
	}

	@Override
	public RandomGenerator<Stream<T>> generator(int genSize) {
		return createListGenerator(genSize, false).map(ReportableStream::new);
	}

	@Override
	public RandomGenerator<Stream<T>> generatorWithEmbeddedEdgeCases(int genSize) {
		return createListGenerator(genSize, true).map(ReportableStream::new);
	}

	@Override
	public Optional<ExhaustiveGenerator<Stream<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators
					   .list(elementArbitrary, minSize, maxSize(), uniquenessExtractors, maxNumberOfSamples)
					   .map(generator -> generator.map(ReportableStream::new));
	}

	@Override
	public EdgeCases<Stream<T>> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.map(
				edgeCases((elements, minSize1) -> new ShrinkableList<>(elements, minSize1, maxSize(), uniquenessExtractors, elementArbitrary), maxEdgeCases),
				ReportableStream::new
		);
	}

	@Override
	public StreamArbitrary<T> ofMaxSize(int maxSize) {
		return (StreamArbitrary<T>) super.ofMaxSize(maxSize);
	}

	@Override
	public StreamArbitrary<T> ofMinSize(int minSize) {
		return (StreamArbitrary<T>) super.ofMinSize(minSize);
	}

	@Override
	public StreamArbitrary<T> withSizeDistribution(RandomDistribution distribution) {
		return (StreamArbitrary<T>) super.withSizeDistribution(distribution);
	}

	@Override
	public StreamArbitrary<T> uniqueElements(Function<? super T, ?> by) {
		FeatureExtractor<T> featureExtractor = by::apply;
		return (StreamArbitrary<T>) super.uniqueElements(featureExtractor);
	}

	@Override
	public StreamArbitrary<T> uniqueElements() {
		return (StreamArbitrary<T>) super.uniqueElements();
	}

}
