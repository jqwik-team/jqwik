package net.jqwik.properties.newShrinking;

import java.util.*;
import java.util.stream.*;

public class ShrinkableList<T> extends NShrinkableValue<List<T>> {
	private final List<NShrinkable<T>> elements;
	private final ListShrinkingCandidates<NShrinkable<T>> shrinkCandidates = new ListShrinkingCandidates<>(0);

	public ShrinkableList(List<NShrinkable<T>> elements) {
		super(createValue(elements));
		this.elements = elements;
	}

	private static <T> List<T> createValue(List<NShrinkable<T>> shrinkables) {
		return shrinkables
			.stream()
			.map(NShrinkable::value)
			.collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
		return super.shrink(falsifier).andThen(shrinkableList -> {
			List<NShrinkable<T>> elements = ((ShrinkableList<T>) shrinkableList).elements;
			return new ElementsShrinkingSequence<>(elements, null, falsifier);
		});
	}

	@Override
	public Set<NShrinkable<List<T>>> shrinkCandidatesFor(NShrinkable<List<T>> shrinkable) {
		ShrinkableList<T> listShrinkable = (ShrinkableList<T>) shrinkable;
		return shrinkCandidates.candidatesFor(listShrinkable.elements)
			.stream()
			.map(shrunkElements -> new ShrinkableList<>(shrunkElements))
			.collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.forCollection(elements);
	}
}
