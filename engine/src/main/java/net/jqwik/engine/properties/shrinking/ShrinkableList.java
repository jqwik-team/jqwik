package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class ShrinkableList<E> extends ShrinkableContainer<List<E>, E> {

	private final int minSize;

	public ShrinkableList(List<Shrinkable<E>> elements, int minSize) {
		super(elements, minSize);
		this.minSize = minSize;
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
		Stream<Shrinkable<List<E>>> shrinkSizeOfList = shrinkSizeOfList();
		Stream<Shrinkable<List<E>>> shrinkElementsOneAfterTheOther = shrinkElementsOneAfterTheOther();
		// Stream<Shrinkable<List<E>>> shrinkPairsOfElements = shrinkPairsOfElements();

		// TODO: Concatenation must be lazy, otherwise stream will explode
		// TODO: stack overflow with long list, never ends with duplicates
		return JqwikStreamSupport.concat(shrinkSizeOfList, shrinkElementsOneAfterTheOther) //, shrinkPairsOfElements)
								 .sorted(Comparator.comparing(Shrinkable::distance));
	}

	private Stream<Shrinkable<List<E>>> shrinkSizeOfList() {
		Set<List<Shrinkable<E>>> shrinkSizeOfListElements = new NEW_ShrinkSizeOfListCandidates<E>(minSize).candidatesFor(elements);
		return shrinkSizeOfListElements.stream().map(this::createShrinkable);
	}

	private Stream<Shrinkable<List<E>>> shrinkElementsOneAfterTheOther() {
		List<Stream<Shrinkable<List<E>>>> shrinkPerElementStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			int index = i;
			Shrinkable<E> element = elements.get(i);
			List<Shrinkable<E>> elementsCopy = new ArrayList<>(elements);
			Stream<Shrinkable<List<E>>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
				elementsCopy.set(index, shrunkElement);
				return Stream.of(createShrinkable(elementsCopy));
			});
			shrinkPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}

	private Stream<Shrinkable<List<E>>> shrinkPairsOfElements() {
		List<Stream<Shrinkable<List<E>>>> shrinkPerElementStreams = new ArrayList<>();

		for (Tuple.Tuple2<Integer, Integer> pair : Combinatorics.distinctPairs(elements.size())) {
			Shrinkable<E> left = elements.get(pair.get1());
			Shrinkable<E> right = elements.get(pair.get2());
			List<Shrinkable<E>> elementsCopy = new ArrayList<>(elements);

			List<Stream<Shrinkable<List<E>>>> shrinkElementStreams =
				JqwikStreamSupport.zip(left.shrink(), right.shrink(),
									   (Shrinkable<E> l, Shrinkable<E> r) -> {
										   elementsCopy.set(pair.get1(), left);
										   elementsCopy.set(pair.get2(), right);
										   return Stream.of(createShrinkable(elementsCopy));
									   }
				).collect(Collectors.toList());
			shrinkPerElementStreams.addAll(shrinkElementStreams);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}
}
