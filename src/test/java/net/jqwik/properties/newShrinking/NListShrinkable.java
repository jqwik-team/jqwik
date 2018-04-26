package net.jqwik.properties.newShrinking;

import java.util.*;
import java.util.stream.*;

public class NListShrinkable<T> implements NShrinkable<List<T>> {
	private final List<NShrinkable<T>> elements;

	public NListShrinkable(List<NShrinkable<T>> elements) {
		this.elements = elements;
	}

	@Override
	public List<T> value() {
		return createValue(elements);
	}

	private List<T> createValue(List<NShrinkable<T>> shrinkables) {
		return shrinkables
			.stream()
			.map(NShrinkable::value)
			.collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
		return null;
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
