package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class ShrinkableSet<E> extends ShrinkableContainer<Set<E>, E> {

	public ShrinkableSet(Set<Shrinkable<E>> elements, int minSize, int maxSize) {
		this(new ArrayList<>(elements), minSize, maxSize);
	}

	private ShrinkableSet(List<Shrinkable<E>> elements, int minSize, int maxSize) {
		super(elements, minSize, maxSize, Collections.emptySet());
	}

	@Override
	public Stream<Shrinkable<Set<E>>> shrink() {
		return super.shrink().filter(shrinkableSet -> shrinkableSet.value().size() >= minSize);
	}

	@Override
	protected boolean hasReallyGrown(Shrinkable<Set<E>> grownShrinkable) {
		return grownShrinkable.value().size() > elements.size();
	}

	@Override
	Collector<E, ?, Set<E>> containerCollector() {
		return Collectors.toSet();
	}

	@Override
	Shrinkable<Set<E>> createShrinkable(List<Shrinkable<E>> shrunkElements) {
		return new ShrinkableSet<>(shrunkElements, minSize, maxSize);
	}
}
