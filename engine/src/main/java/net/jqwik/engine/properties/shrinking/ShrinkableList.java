package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class ShrinkableList<E> extends ShrinkableContainer<List<E>, E> {

	public ShrinkableList(List<Shrinkable<E>> elements, int minSize, int maxSize) {
		super(elements, minSize, maxSize);
	}

	@Override
	Collector<E, ?, List<E>> containerCollector() {
		return Collectors.toList();
	}

	@Override
	Shrinkable<List<E>> createShrinkable(List<Shrinkable<E>> shrunkElements) {
		return new ShrinkableList<>(shrunkElements, minSize, maxSize);
	}

	@Override
	public Stream<Shrinkable<List<E>>> shrink() {
		return JqwikStreamSupport.concat(
			super.shrink(),
			sortElements(),
			moveIndividualValuesTowardsEnd()
		);
	}

	private Stream<Shrinkable<List<E>>> moveIndividualValuesTowardsEnd() {
		List<Shrinkable<List<E>>> moves = new ArrayList<>();
		for (Tuple.Tuple2<Integer, Integer> pair : Combinatorics.distinctPairs(elements.size())) {
			int firstIndex = Math.min(pair.get1(), pair.get2());
			int secondIndex = Math.max(pair.get1(), pair.get2());
			Shrinkable<E> first = elements.get(firstIndex);
			Shrinkable<E> second = elements.get(secondIndex);
			if (first.compareTo(second) > 0) {
				continue;
			}
			first.shrink()
				 .map(after -> {
					 Optional<Shrinkable<E>> grow = second.grow(first, after);
					 return Tuple.of(after, grow);
				 })
				 .filter(tuple -> tuple.get2().isPresent())
				 .forEach(tuple -> {
					 List<Shrinkable<E>> pairMove = new ArrayList<>(elements);
					 pairMove.set(firstIndex, tuple.get1());
					 pairMove.set(secondIndex, tuple.get2().get());
					 moves.add(createShrinkable(pairMove));
				 });
		}
		return moves.stream();
	}
}
