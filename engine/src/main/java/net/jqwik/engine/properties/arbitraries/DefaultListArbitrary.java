package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

import static net.jqwik.engine.properties.UniquenessChecker.*;

public class DefaultListArbitrary<T> extends MultivalueArbitraryBase<T, List<T>> implements ListArbitrary<T> {

	public DefaultListArbitrary(Arbitrary<T> elementArbitrary, boolean elementsUnique) {
		super(elementArbitrary, elementsUnique);
	}

	@Override
	protected Iterable<T> toIterable(List<T> streamable) {
		return streamable;
	}

	@Override
	public RandomGenerator<List<T>> generator(int genSize) {
		return createListGenerator(genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<List<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.list(elementArbitrary, minSize, maxSize, maxNumberOfSamples)
								   .map(generator -> generator.filter(l -> checkUniquenessOfValues(uniquenessExtractors, l)));
	}

	@Override
	public EdgeCases<List<T>> edgeCases() {
		return edgeCases((elements, minSize1) -> new ShrinkableList<T>(elements, minSize1, maxSize, uniquenessExtractors));
	}

	@Override
	public ListArbitrary<T> ofMaxSize(int maxSize) {
		return (ListArbitrary<T>) super.ofMaxSize(maxSize);
	}

	@Override
	public ListArbitrary<T> ofMinSize(int minSize) {
		return (ListArbitrary<T>) super.ofMinSize(minSize);
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
	public ListArbitrary<T> uniqueness(Function<T, Object> by) {
		FeatureExtractor<T> featureExtractor = by::apply;
		return (ListArbitrary<T>) super.uniqueness(featureExtractor);
	}

	@Override
	public ListArbitrary<T> uniqueElements() {
		return (ListArbitrary<T>) uniqueness(FeatureExtractor.identity());
	}
}
