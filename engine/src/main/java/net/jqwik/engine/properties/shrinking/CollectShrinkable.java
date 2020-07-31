package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class CollectShrinkable<T> implements Shrinkable<List<T>> {
	private final List<Shrinkable<T>> elements;
	private final Predicate<List<T>> until;

	public CollectShrinkable(List<Shrinkable<T>> elements, Predicate<List<T>> until) {
		this.elements = elements;
		this.until = until;
	}

	@Override
	public List<T> value() {
		return createValue(elements);
	}

	@Override
	public List<T> createValue() {
		return value();
	}

	private List<T> createValue(List<Shrinkable<T>> elements) {
		return elements
				   .stream()
				   .map(Shrinkable::value)
				   .collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
		return new CollectShrinkingSequence<>(elements, until, falsifier);
	}

	@Override
	public Stream<Shrinkable<List<T>>> shrink() {
		return JqwikStreamSupport.lazyConcat(
			this::shrinkElementsOneAfterTheOther,
			this::sortElements
		).filter(s -> until.test(s.createValue()));
	}

	private Stream<Shrinkable<List<T>>> shrinkElementsOneAfterTheOther() {
		List<Stream<Shrinkable<List<T>>>> shrinkPerPartStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			int index = i;
			Shrinkable<T> part = elements.get(i);
			Stream<Shrinkable<List<T>>> shrinkElement = part.shrink().flatMap(shrunkElement -> {
				Optional<List<Shrinkable<T>>> shrunkCollection = collectElements(index, shrunkElement);
				return shrunkCollection
						   .map(shrunkElements -> Stream.of(createShrinkable(shrunkElements)))
						   .orElse(Stream.empty());
			});
			shrinkPerPartStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerPartStreams);
	}

	// TODO: Remove duplication with ShrinkableContainer.sortElements() and
	protected Stream<Shrinkable<List<T>>> sortElements() {
		List<Shrinkable<T>> sortedElements = new ArrayList<>(elements);
		sortedElements.sort(Comparator.comparing(Shrinkable::distance));
		if (elements.equals(sortedElements)) {
			return Stream.empty();
		}
		return JqwikStreamSupport.lazyConcat(
			() -> fullSort(sortedElements),
			() -> pairwiseSort(elements)
		);
	}

	private Stream<Shrinkable<List<T>>> fullSort(List<Shrinkable<T>> sortedElements) {
		return Stream.of(createShrinkable(sortedElements));
	}

	private Stream<Shrinkable<List<T>>> pairwiseSort(List<Shrinkable<T>> elements) {
		List<Shrinkable<List<T>>> swaps = new ArrayList<>();
		for (Tuple.Tuple2<Integer, Integer> pair : Combinatorics.distinctPairs(elements.size())) {
			int firstIndex = Math.min(pair.get1(), pair.get2());
			int secondIndex = Math.max(pair.get1(), pair.get2());
			Shrinkable<T> first = elements.get(firstIndex);
			Shrinkable<T> second = elements.get(secondIndex);
			if (first.compareTo(second) > 0) {
				List<Shrinkable<T>> pairSwap = new ArrayList<>(elements);
				pairSwap.set(firstIndex, second);
				pairSwap.set(secondIndex, first);
				swaps.add(createShrinkable(pairSwap));
			}
		}
		return swaps.stream();
	}

	private CollectShrinkable<T> createShrinkable(List<Shrinkable<T>> pairSwap) {
		return new CollectShrinkable<>(pairSwap, until);
	}

	private Optional<List<Shrinkable<T>>> collectElements(int replaceIndex, Shrinkable<T> shrunkElement) {
		List<Shrinkable<T>> newElements = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			if (i == replaceIndex) {
				newElements.add(shrunkElement);
			} else {
				newElements.add(elements.get(i));
			}
			if (until.test(createValue(newElements))) {
				return Optional.of(newElements);
			}
		}
		return Optional.empty();
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.forCollection(elements);
	}
}
