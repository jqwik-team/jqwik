package net.jqwik.properties.newShrinking;

import java.util.*;
import java.util.stream.*;

public class ShrinkableList<E> implements NShrinkable<List<E>> {
	private final Collector<E, ?, List<E>> collectionCollector;
	private final List<NShrinkable<E>> elements;
	private final ListShrinkingCandidates<NShrinkable<E>> shrinkCandidates;

	public ShrinkableList(List<NShrinkable<E>> elements, int minSize) {
		this.elements = elements;
		this.shrinkCandidates = new ListShrinkingCandidates<>(minSize);
		this.collectionCollector = Collectors.toList();
	}

	private List<E> createValue(List<NShrinkable<E>> shrinkables) {
		return shrinkables
			.stream().map(NShrinkable::value).collect(collectionCollector);
	}

	@Override
	public List<E> value() {
		return createValue(elements);
	}

	@Override
	public ShrinkingSequence<List<E>> shrink(Falsifier<List<E>> falsifier) {
		return new DeepSearchShrinkingSequence<>(this, this::shrinkCandidatesFor, falsifier) //
			.andThen(shrinkableList -> { //
				List<NShrinkable<E>> elements = ((ShrinkableList<E>) shrinkableList).elements;
				return new ElementsShrinkingSequence<>(elements, null, falsifier, ShrinkingDistance::forCollection);
			});
	}

	private Set<NShrinkable<List<E>>> shrinkCandidatesFor(NShrinkable<List<E>> shrinkable) {
		ShrinkableList<E> listShrinkable = (ShrinkableList<E>) shrinkable;
		return shrinkCandidates.candidatesFor(listShrinkable.elements).stream().map(shrunkElements -> new ShrinkableList<>(shrunkElements, 0))
			.collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.forCollection(elements);
	}
}
