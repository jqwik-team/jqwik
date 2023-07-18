package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

import org.jspecify.annotations.*;

public class DefaultListArbitrary<T> extends MultivalueArbitraryBase<T, List<T>> implements ListArbitrary<T> {

	public DefaultListArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	protected Iterable<T> toIterable(List<T> streamable) {
		return streamable;
	}

	@Override
	public RandomGenerator<List<T>> generator(int genSize) {
		return createListGenerator(genSize, false);
	}

	@Override
	public RandomGenerator<List<T>> generatorWithEmbeddedEdgeCases(int genSize) {
		return createListGenerator(genSize, true);
	}

	@Override
	public Optional<ExhaustiveGenerator<List<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.list(elementArbitrary, minSize, maxSize(), uniquenessExtractors, maxNumberOfSamples);
	}

	@Override
	public EdgeCases<List<T>> edgeCases(int maxEdgeCases) {
		return edgeCases((elements, minSize1) -> new ShrinkableList<>(elements, minSize1, maxSize(), uniquenessExtractors, elementArbitrary), maxEdgeCases);
	}

	@Override
	public ListArbitrary<T> ofMaxSize(int maxSize) {
		return (ListArbitrary<T>) super.ofMaxSize(maxSize);
	}

	@Override
	public ListArbitrary<T> ofMinSize(int minSize) {
		return (ListArbitrary<T>) super.ofMinSize(minSize);
	}

	@Override
	public ListArbitrary<T> withSizeDistribution(RandomDistribution distribution) {
		return (ListArbitrary<T>) super.withSizeDistribution(distribution);
	}

	// TODO: Remove duplication with DefaultSetArbitrary.mapEach()
	@Override
	public <U> Arbitrary<List<U>> mapEach(BiFunction<List<T>, T, U> mapper) {
		return this.map(elements -> elements.stream()
											.map(e -> mapper.apply(elements, e))
											.collect(Collectors.toList()));
	}

	// TODO: Remove duplication with DefaultSetArbitrary.flatMapEach()
	@Override
	public <U> Arbitrary<List<U>> flatMapEach(BiFunction<List<T>, T, Arbitrary<U>> flatMapper) {
		return this.flatMap(elements -> {
			List<Arbitrary<U>> arbitraries =
				elements.stream()
						.map(e -> flatMapper.apply(elements, e))
						.collect(Collectors.toList());
			return Combinators.combine(arbitraries).as(ArrayList::new);
		});
	}

	@Override
	public ListArbitrary<@Nullable T> uniqueElements(Function<@Nullable T, Object> by) {
		FeatureExtractor<T> featureExtractor = by::apply;
		return (ListArbitrary<T>) super.uniqueElements(featureExtractor);
	}

	@Override
	public ListArbitrary<T> uniqueElements() {
		return (ListArbitrary<T>) super.uniqueElements();
	}
}
