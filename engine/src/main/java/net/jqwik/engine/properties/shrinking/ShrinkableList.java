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
		ShrinkingDistance distance = distance();
		return Combinatorics
				   .distinctPairs(elements.size())
				   .map(pair -> {
					   int firstIndex = Math.min(pair.get1(), pair.get2());
					   int secondIndex = Math.max(pair.get1(), pair.get2());
					   Shrinkable<E> first = elements.get(firstIndex);
					   Shrinkable<E> second = elements.get(secondIndex);
					   return Tuple.of(firstIndex, first, secondIndex, second);
				   })
				   .filter(quadruple -> quadruple.get2().compareTo(quadruple.get4()) <= 0)
				   .flatMap(quadruple -> {
					   int firstIndex = quadruple.get1();
					   Shrinkable<E> first = quadruple.get2();
					   int secondIndex = quadruple.get3();
					   Shrinkable<E> second = quadruple.get4();
					   return first.shrink()
								   .map(after -> {
									   Optional<Shrinkable<E>> grow = second.grow(first, after);
									   return Tuple.of(after, grow);
								   })
								   .filter(tuple -> tuple.get2().isPresent())
								   .map(tuple -> {
									   List<Shrinkable<E>> pairMove = new ArrayList<>(elements);
									   pairMove.set(firstIndex, tuple.get1());
									   pairMove.set(secondIndex, tuple.get2().get());
									   return createShrinkable(pairMove);
								   });

				   })
				   // In rare cases of nested lists shrinkGrow can increase the distance
				   .filter(s -> s.distance().compareTo(distance) <= 0);
	}
}
