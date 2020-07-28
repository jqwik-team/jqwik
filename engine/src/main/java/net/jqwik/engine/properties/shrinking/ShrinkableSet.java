package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class ShrinkableSet<E> extends ShrinkableContainer<Set<E>, E> {

	public ShrinkableSet(Set<Shrinkable<E>> elements, int minSize) {
		this(new ArrayList<>(elements), minSize);
	}

	private ShrinkableSet(List<Shrinkable<E>> elements, int minSize) {
		super(elements, minSize);
	}

	@Override
	public ShrinkingSequence<Set<E>> shrink(Falsifier<Set<E>> falsifier) {
		return super.shrink(falsifier.withFilter(set -> set.size() >= minSize));
	}

	@Override
	public Stream<Shrinkable<Set<E>>> shrink() {
		return super.shrink().filter(shrinkableSet -> shrinkableSet.createValue().size() >= minSize);
	}

	@Override
	Collector<E, ?, Set<E>> containerCollector() {
		return Collectors.toSet();
	}

	@Override
	Shrinkable<Set<E>> createShrinkable(List<Shrinkable<E>> shrunkElements) {
		return new ShrinkableSet<>(shrunkElements, minSize);
	}
}
