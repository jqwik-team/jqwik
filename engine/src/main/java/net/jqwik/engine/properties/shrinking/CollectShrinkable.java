package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class CollectShrinkable<T> implements Shrinkable<List<T>> {
	private final List<T> value;
	private final List<Shrinkable<T>> elements;
	private final Predicate<List<T>> until;

	public CollectShrinkable(List<Shrinkable<T>> elements, Predicate<List<T>> until) {
		this.value = createValue(elements);
		this.elements = elements;
		this.until = until;
	}

	@Override
	public List<T> value() {
		return value;
	}

	private List<T> createValue(List<Shrinkable<T>> elements) {
		return elements
				   .stream()
				   .map(Shrinkable::value)
				   .collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
		return new CollectShrinkingSequence<>(elements, until, falsifier);
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.forCollection(elements);
	}
}
