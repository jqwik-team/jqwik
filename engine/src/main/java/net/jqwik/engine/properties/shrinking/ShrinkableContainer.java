package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

abstract class ShrinkableContainer<C, E> implements Shrinkable<C> {
	protected final List<Shrinkable<E>> elements;
	protected final int minSize;

	ShrinkableContainer(List<Shrinkable<E>> elements, int minSize) {
		this.elements = elements;
		this.minSize = minSize;
	}

	private C createValue(List<Shrinkable<E>> shrinkables) {
		return shrinkables
				   .stream()
				   .map(Shrinkable::value)
				   .collect(containerCollector());
	}

	@Override
	public C value() {
		return createValue(elements);
	}

	@Override
	public Stream<Shrinkable<C>> shrink() {
		return JqwikStreamSupport.concat(
			shrinkSizeOfList(),
			shrinkElementsOneAfterTheOther(),
			shrinkPairsOfElements()
		);
	}

	protected Stream<Shrinkable<C>> shrinkSizeOfList() {
		return new SizeOfListShrinker<Shrinkable<E>>(minSize)
				   .shrink(elements)
				   .map(this::createShrinkable)
				   .sorted(Comparator.comparing(Shrinkable::distance));
	}

	protected Stream<Shrinkable<C>> shrinkElementsOneAfterTheOther() {
		List<Stream<Shrinkable<C>>> shrinkPerElementStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			int index = i;
			Shrinkable<E> element = elements.get(i);
			Stream<Shrinkable<C>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
				List<Shrinkable<E>> elementsCopy = new ArrayList<>(elements);
				elementsCopy.set(index, shrunkElement);
				return Stream.of(createShrinkable(elementsCopy));
			});
			shrinkPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}

	protected Stream<Shrinkable<C>> shrinkPairsOfElements() {
		if (elements.size() < 2) {
			return Stream.empty();
		}
		List<Supplier<Stream<Shrinkable<C>>>> suppliers = new ArrayList<>();
		for (Tuple.Tuple2<Integer, Integer> pair : Combinatorics.distinctPairs(elements.size())) {
			Supplier<Stream<Shrinkable<C>>> zip = () -> JqwikStreamSupport.zip(
				elements.get(pair.get1()).shrink(),
				elements.get(pair.get2()).shrink(),
				(Shrinkable<E> s1, Shrinkable<E> s2) -> {
					List<Shrinkable<E>> newElements = new ArrayList<>(elements);
					newElements.set(pair.get1(), s1);
					newElements.set(pair.get2(), s2);
					return createShrinkable(newElements);
				}
			);
			suppliers.add(zip);
		}
		return JqwikStreamSupport.lazyConcat(suppliers);
	}

	// TODO: Remove duplication with ShrinkableContainer.sortElements() and
	protected Stream<Shrinkable<C>> sortElements() {
		List<Shrinkable<E>> sortedElements = new ArrayList<>(elements);
		sortedElements.sort(Comparator.comparing(Shrinkable::distance));
		if (elements.equals(sortedElements)) {
			return Stream.empty();
		}
		return JqwikStreamSupport.concat(
			fullSort(sortedElements),
			pairwiseSort(elements)
		);
	}

	private Stream<Shrinkable<C>> fullSort(List<Shrinkable<E>> sortedElements) {
		return Stream.of(createShrinkable(sortedElements));
	}

	private Stream<Shrinkable<C>> pairwiseSort(List<Shrinkable<E>> elements) {
		List<Shrinkable<C>> swaps = new ArrayList<>();
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

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.forCollection(elements);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShrinkableContainer<?, ?> that = (ShrinkableContainer<?, ?>) o;
		return value().equals(that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(elements);
	}

	@Override
	public String toString() {
		return String.format(
			"%s<%s>(%s:%s)",
			getClass().getSimpleName(),
			value().getClass().getSimpleName(),
			value(), distance()
		);
	}

	abstract Shrinkable<C> createShrinkable(List<Shrinkable<E>> shrunkElements);

	abstract Collector<E, ?, C> containerCollector();

}
