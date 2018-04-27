package net.jqwik.properties.newShrinking;

import java.util.*;
import java.util.stream.*;

public class NListShrinkable<T> extends NShrinkableValue<List<T>> {
	private final List<NShrinkable<T>> elements;
	private final NListShrinkCandidates<NShrinkable<T>> shrinkCandidates = new NListShrinkCandidates<>(0);

	public NListShrinkable(List<NShrinkable<T>> elements) {
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
			return ShrinkingSequence.dontShrink(shrinkableList);
		});
	}

	@Override
	public Set<NShrinkable<List<T>>> shrinkCandidatesFor(NShrinkable<List<T>> shrinkable) {
		NListShrinkable<T> listShrinkable = (NListShrinkable<T>) shrinkable;
		return shrinkCandidates.candidatesFor(listShrinkable.elements)
			.stream()
			.map(shrunkElements -> new NListShrinkable<>(shrunkElements))
			.collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		ShrinkingDistance sumDistanceOfElements = elements
			.stream()
			.map(NShrinkable::distance)
			.reduce(ShrinkingDistance.of(0), ShrinkingDistance::plus);

		return ShrinkingDistance.of(elements.size()).append(sumDistanceOfElements);
	}
}
