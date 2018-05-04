package net.jqwik.properties.newShrinking;

import java.util.*;
import java.util.stream.*;

public class ShrinkableSet<E> extends ShrinkableContainer<Set<E>, E> {

	private final int minSize;

	public ShrinkableSet(Set<NShrinkable<E>> elements, int minSize) {
		this(new ArrayList<>(elements), minSize);
	}

	private ShrinkableSet(List<NShrinkable<E>> elements, int minSize) {
		super(elements, minSize);
		this.minSize = minSize;
	}

	@Override
	public ShrinkingSequence<Set<E>> shrink(Falsifier<Set<E>> falsifier) {
		return super.shrink(falsifier.withFilter(set -> set.size() >= minSize));
	}

	@Override
	Collector<E, ?, Set<E>> containerCollector() {
		return Collectors.toSet();
	}

	@Override
	NShrinkable<Set<E>> createShrinkable(List<NShrinkable<E>> shrunkElements) {
		return new ShrinkableSet<>(shrunkElements, minSize);
	}
}
