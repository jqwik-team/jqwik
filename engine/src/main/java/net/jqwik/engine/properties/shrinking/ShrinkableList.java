package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class ShrinkableList<E> extends ShrinkableContainer<List<E>, E> {

	public ShrinkableList(List<Shrinkable<E>> elements, int minSize) {
		super(elements, minSize);
	}

	@Override
	Collector<E, ?, List<E>> containerCollector() {
		return Collectors.toList();
	}

	@Override
	Shrinkable<List<E>> createShrinkable(List<Shrinkable<E>> shrunkElements) {
		return new ShrinkableList<>(shrunkElements, minSize);
	}

	@Override
	public Stream<Shrinkable<List<E>>> shrink() {
		return JqwikStreamSupport.lazyConcat(
			super::shrink,
			this::sortElements
		);
	}

	private Stream<Shrinkable<List<E>>> sortElements() {
		List<Shrinkable<E>> sortedElements = new ArrayList<>(elements);
		sortedElements.sort(Comparator.comparing(Shrinkable::distance));
		if (elements.equals(sortedElements)) {
			return Stream.empty();
		}
		return JqwikStreamSupport.lazyConcat(
			() -> fullSort(sortedElements),
			() -> pairwiseSort(elements)
		);
	}

	private Stream<Shrinkable<List<E>>> fullSort(List<Shrinkable<E>> sortedElements) {
		return Stream.of(createShrinkable(sortedElements));
	}

	private Stream<Shrinkable<List<E>>> pairwiseSort(List<Shrinkable<E>> elements) {
		List<Shrinkable<List<E>>> swaps = new ArrayList<>();
		for (Tuple.Tuple2<Integer, Integer> pair : Combinatorics.distinctPairs(elements.size())) {
			int firstIndex = Math.min(pair.get1(), pair.get2());
			int secondIndex = Math.max(pair.get1(), pair.get2());
			Shrinkable<E> first = elements.get(firstIndex);
			Shrinkable<E> second = elements.get(secondIndex);
			if (first.compareTo(second) > 0) {
				List<Shrinkable<E>> pairSwap = new ArrayList<>(elements);
				pairSwap.set(firstIndex, second);
				pairSwap.set(secondIndex, first);
				swaps.add(createShrinkable(pairSwap));
			}
		}
		return swaps.stream();
	}
}
