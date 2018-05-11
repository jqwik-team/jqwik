package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

public class ShrinkableList<E> extends ShrinkableContainer<List<E>, E> {

	private final int minSize;

	public ShrinkableList(List<NShrinkable<E>> elements, int minSize) {
		super(elements, minSize);
		this.minSize = minSize;
	}

	@Override
	Collector<E, ?, List<E>> containerCollector() {
		return Collectors.toList();
	}

	@Override
	NShrinkable<List<E>> createShrinkable(List<NShrinkable<E>> shrunkElements) {
		return new ShrinkableList<>(shrunkElements, minSize);
	}
}
