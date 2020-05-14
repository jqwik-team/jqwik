package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

import static net.jqwik.engine.properties.arbitraries.ArbitrariesSupport.*;

abstract class MultivalueArbitraryBase<T, U> extends AbstractArbitraryBase implements StreamableArbitrary<T, U> {

	protected Arbitrary<T> elementArbitrary;
	protected int minSize = 0;
	protected int maxSize = RandomGenerators.DEFAULT_COLLECTION_SIZE;
	private final boolean elementsUnique;

	protected MultivalueArbitraryBase(Arbitrary<T> elementArbitrary, boolean elementsUnique) {
		this.elementArbitrary = elementArbitrary;
		this.elementsUnique = elementsUnique;
		if (elementsUnique) {
			this.maxSize = maxNumberOfElements(elementArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE);
		}
	}

	@Override
	public StreamableArbitrary<T, U> ofMinSize(int minSize) {
		MultivalueArbitraryBase<T, U> clone = typedClone();
		clone.minSize = minSize;
		return clone;
	}

	@Override
	public StreamableArbitrary<T, U> ofMaxSize(int maxSize) {
		MultivalueArbitraryBase<T, U> clone = typedClone();
		clone.maxSize = maxSize;
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

	protected abstract Iterable<T> toIterable(U streamable);

	protected RandomGenerator<List<T>> createListGenerator(int genSize) {
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, genSize);
		EdgeCases<List<T>> edgeCases = edgeCases(ShrinkableList::new);
		return RandomGenerators
				   .list(elementGenerator, minSize, maxSize, cutoffSize(genSize))
				   .withEdgeCases(genSize, edgeCases);
	}

	protected int cutoffSize(int genSize) {
		return RandomGenerators.defaultCutoffSize(minSize, maxSize, genSize);
	}

	protected RandomGenerator<T> elementGenerator(Arbitrary<T> elementArbitrary, int genSize) {
		return elementArbitrary.generator(genSize);
	}

	protected <C extends Collection<?>> EdgeCases<C> edgeCases(BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator) {
		EdgeCases<C> emptyListEdgeCase = (minSize == 0) ? emptyListEdgeCase(shrinkableCreator) : EdgeCases.none();
		EdgeCases<C> singleElementEdgeCases = (minSize <= 1 && maxSize >= 1) ? fixedSizeEdgeCases(1, shrinkableCreator) : EdgeCases.none();
		EdgeCases<C> fixedSizeEdgeCases = generateFixedSizeEdgeCases() ? fixedSizeEdgeCases(minSize, shrinkableCreator) : EdgeCases.none();
		return EdgeCases.concat(Arrays.asList(emptyListEdgeCase, singleElementEdgeCases, fixedSizeEdgeCases));
	}

	private boolean generateFixedSizeEdgeCases() {
		if (elementsUnique) {
			return false;
		}
		return minSize == maxSize && minSize > 1;
	}

	private <C extends Collection<?>> EdgeCases<C> fixedSizeEdgeCases(
		final int fixedSize,
		final BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator
	) {
		return elementArbitrary
				   .edgeCases()
				   .mapShrinkable((Shrinkable<T> shrinkableT) -> {
					   List<Shrinkable<T>> elements = new ArrayList<>(Collections.nCopies(fixedSize, shrinkableT));
					   return shrinkableCreator.apply(elements, minSize);
				   });
	}

	private <C extends Collection<?>> EdgeCases<C> emptyListEdgeCase(BiFunction<List<Shrinkable<T>>, Integer, Shrinkable<C>> shrinkableCreator) {
		return EdgeCases.fromSupplier(
			() -> shrinkableCreator.apply(Collections.emptyList(), minSize)
		);
	}

}
