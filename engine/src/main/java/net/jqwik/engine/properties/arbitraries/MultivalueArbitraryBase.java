package net.jqwik.engine.properties.arbitraries;

import java.util.ArrayList;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

import static java.util.Arrays.*;

import static net.jqwik.engine.properties.UniquenessChecker.*;

abstract class MultivalueArbitraryBase<T, U> extends TypedCloneable implements StreamableArbitrary<T, U> {

	protected Arbitrary<T> elementArbitrary;

	protected int minSize = 0;
	private Integer maxSize = null;
	protected Set<FeatureExtractor<T>> uniquenessExtractors = new LinkedHashSet<>();
	protected RandomDistribution sizeDistribution = null;

	protected MultivalueArbitraryBase(Arbitrary<T> elementArbitrary) {
		this.elementArbitrary = elementArbitrary;
	}

	@Override
	public boolean isGeneratorMemoizable() {
		return elementArbitrary.isGeneratorMemoizable();
	}

	@Override
	public StreamableArbitrary<T, U> ofMinSize(int minSize) {
		if (minSize < 0) {
			String message = String.format("minSize (%s) must be between 0 and 2147483647", minSize);
			throw new IllegalArgumentException(message);
		}

		MultivalueArbitraryBase<T, U> clone = typedClone();
		clone.minSize = minSize;
		return clone;
	}

	@Override
	public StreamableArbitrary<T, U> ofMaxSize(int maxSize) {
		if (maxSize < 0) {
			String message = String.format("maxSize (%s) must be between 0 and 2147483647", maxSize);
			throw new IllegalArgumentException(message);
		}
		if (maxSize < minSize) {
			String message = String.format("minSize (%s) must not be larger than maxSize (%s)", minSize, maxSize);
			throw new IllegalArgumentException(message);
		}

		MultivalueArbitraryBase<T, U> clone = typedClone();
		clone.maxSize = maxSize;
		return clone;
	}

	@Override
	public StreamableArbitrary<T, U> withSizeDistribution(RandomDistribution distribution) {
		MultivalueArbitraryBase<T, U> clone = typedClone();
		clone.sizeDistribution = distribution;
		return clone;
	}

	@Override
	public <R> Arbitrary<R> reduce(R initial, BiFunction<R, T, R> accumulator) {
		return this.map(streamable -> {
			// Couldn't find a way to use Stream.reduce since it requires a combinator
			@SuppressWarnings("unchecked")
			R[] result = (R[]) new Object[]{initial};
			Iterable<T> iterable = toIterable(streamable);
			for (T each : iterable) {
				result[0] = accumulator.apply(result[0], each);
			}
			return result[0];
		});
	}

	@Override
	public StreamableArbitrary<T, U> uniqueElements() {
		return uniqueElements(FeatureExtractor.identity());
	}

	protected abstract Iterable<T> toIterable(U streamable);

	protected StreamableArbitrary<T, U> uniqueElements(FeatureExtractor<T> by) {
		MultivalueArbitraryBase<T, U> clone = typedClone();
		clone.uniquenessExtractors = new LinkedHashSet<>(uniquenessExtractors);
		clone.uniquenessExtractors.add(by);
		return clone;
	}

	protected RandomGenerator<List<T>> createListGenerator(int genSize, boolean withEmbeddedEdgeCases) {
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, genSize, withEmbeddedEdgeCases);
		long maxUniqueElements = elementArbitrary.exhaustive(maxSize()).map(ExhaustiveGenerator::maxCount).orElse((long) maxSize());
		return RandomGenerators.list(elementGenerator, minSize, maxSize(), maxUniqueElements, genSize, sizeDistribution, uniquenessExtractors, elementArbitrary);
	}

	protected RandomGenerator<T> elementGenerator(Arbitrary<T> elementArbitrary, int genSize, boolean withEdgeCases) {
		return elementArbitrary.generator(genSize, withEdgeCases);
	}

	protected <C extends Collection<?>> EdgeCases<C> edgeCases(
		BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator,
		int maxEdgeCases
	) {
		// Optimization. Already handled by EdgeCases.concat(..)
		if (maxEdgeCases <= 0) {
			return EdgeCases.none();
		}

		EdgeCases<C> emptyListEdgeCase = (minSize == 0) ? emptyListEdgeCase(shrinkableCreator) : EdgeCases.none();

		int effectiveMaxEdgeCases = maxEdgeCases - emptyListEdgeCase.size();
		EdgeCases<C> singleElementEdgeCases = (minSize <= 1 && maxSize() >= 1)
												  ? fixedSizeEdgeCases(1, shrinkableCreator, effectiveMaxEdgeCases)
												  : EdgeCases.none();

		effectiveMaxEdgeCases = maxEdgeCases - singleElementEdgeCases.size();
		EdgeCases<C> fixedSizeEdgeCases = generateFixedSizeEdgeCases()
											  ? fixedSizeEdgeCases(minSize, shrinkableCreator, effectiveMaxEdgeCases)
											  : EdgeCases.none();

		return EdgeCasesSupport.concat(asList(emptyListEdgeCase, singleElementEdgeCases, fixedSizeEdgeCases), maxEdgeCases);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MultivalueArbitraryBase<?, ?> that = (MultivalueArbitraryBase<?, ?>) o;
		if (minSize != that.minSize) return false;
		if (!Objects.equals(maxSize, that.maxSize)) return false;
		if (!elementArbitrary.equals(that.elementArbitrary)) return false;
		if (!uniquenessExtractors.equals(that.uniquenessExtractors)) return false;
		return Objects.equals(sizeDistribution, that.sizeDistribution);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(elementArbitrary, minSize, maxSize, uniquenessExtractors);
	}

	private boolean generateFixedSizeEdgeCases() {
		return minSize == maxSize() && minSize > 1;
	}

	private <C extends Collection<?>> EdgeCases<C> fixedSizeEdgeCases(
		final int fixedSize,
		final BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator,
		int maxEdgeCases
	) {
		return EdgeCasesSupport.mapShrinkable(
			elementArbitrary.edgeCases(maxEdgeCases),
			shrinkableT -> {
				List<Shrinkable<T>> elements = new ArrayList<>(Collections.nCopies(fixedSize, shrinkableT));
				if (!checkUniquenessOfShrinkables(uniquenessExtractors, elements)) {
					return null;
				}
				return shrinkableCreator.apply(elements, minSize);
			}
		);
	}

	private <C extends Collection<?>> EdgeCases<C> emptyListEdgeCase(BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator) {
		return EdgeCases.fromSupplier(
			() -> shrinkableCreator.apply(Collections.emptyList(), minSize)
		);
	}

	protected int maxSize() {
		return RandomGenerators.collectionMaxSize(minSize, maxSize);
	}
}
