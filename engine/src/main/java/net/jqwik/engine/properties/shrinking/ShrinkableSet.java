package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

import static net.jqwik.engine.support.JqwikCollectors.toLinkedHashSet;

public class ShrinkableSet<E> extends ShrinkableContainer<Set<E>, E> {

	public ShrinkableSet(Collection<Shrinkable<E>> elements, int minSize, int maxSize, Collection<FeatureExtractor<E>> uniquenessExtractors) {
		this(new ArrayList<>(elements), minSize, maxSize, uniquenessExtractors);
	}

	private ShrinkableSet(List<Shrinkable<E>> elements, int minSize, int maxSize, Collection<FeatureExtractor<E>> uniquenessExtractors) {
		super(elements, minSize, maxSize, uniquenessExtractors);
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
		return toLinkedHashSet();
	}

	@Override
	Shrinkable<Set<E>> createShrinkable(List<Shrinkable<E>> shrunkElements) {
		return new ShrinkableSet<>(shrunkElements, minSize, maxSize, uniquenessExtractors);
	}
}
