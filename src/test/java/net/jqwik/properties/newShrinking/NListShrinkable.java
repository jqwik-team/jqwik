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
		//TODO: Shrink elements afterwards
		return super.shrink(falsifier);
	}

	@Override
	public Set<NShrinkable<List<T>>> shrinkCandidatesFor(NShrinkable<List<T>> shrinkable) {
		return null;
//		return shrinkCandidates.candidatesFor(shrinkable.value())
//			.stream()
//			.map(list -> new NListShrinkable<>(list))
//			.collect(Collectors.toSet());
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
