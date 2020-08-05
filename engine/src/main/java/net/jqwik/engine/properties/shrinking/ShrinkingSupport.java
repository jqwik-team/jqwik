package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

class ShrinkingSupport {

	@FunctionalInterface
	interface ContainerCreator<C, E> extends Function<List<Shrinkable<E>>, Shrinkable<C>> {}

	/**
	 * Sort elements of container pairwise
	 *
	 * @param elements        Unsorted elements
	 * @param createContainer function to create shrinkable container
	 * @param <C>             type of container
	 * @param <E>             type of elements
	 * @return stream of shrunk containers
	 */
	static <C, E> Stream<Shrinkable<C>> sortElements(List<Shrinkable<E>> elements, ContainerCreator<C, E> createContainer) {
		List<Shrinkable<E>> sortedElements = new ArrayList<>(elements);
		sortedElements.sort(Comparator.comparing(Shrinkable::distance));
		if (elements.equals(sortedElements)) {
			return Stream.empty();
		}
		return JqwikStreamSupport.concat(
			fullSort(sortedElements, createContainer),
			pairwiseSort(elements, createContainer)
		);
	}

	private static <C, E> Stream<Shrinkable<C>> fullSort(List<Shrinkable<E>> sortedElements, ContainerCreator<C, E> createContainer) {
		return Stream.of(createContainer.apply(sortedElements));
	}

	private static <C, E> Stream<Shrinkable<C>> pairwiseSort(List<Shrinkable<E>> elements, ContainerCreator<C, E> createContainer) {
		return Combinatorics.distinctPairs(elements.size())
							.map(pair -> {
								int firstIndex = Math.min(pair.get1(), pair.get2());
								int secondIndex = Math.max(pair.get1(), pair.get2());
								Shrinkable<E> first = elements.get(firstIndex);
								Shrinkable<E> second = elements.get(secondIndex);
								return Tuple.of(firstIndex, first, secondIndex, second);
							})
							.filter(quadruple -> quadruple.get2().compareTo(quadruple.get4()) > 0)
							.map(quadruple -> {
								List<Shrinkable<E>> pairSwap = new ArrayList<>(elements);
								pairSwap.set(quadruple.get1(), quadruple.get4());
								pairSwap.set(quadruple.get3(), quadruple.get2());
								return createContainer.apply(pairSwap);
							});
	}
}
