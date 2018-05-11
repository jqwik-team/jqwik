package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

public class ShrinkableList<E> extends ShrinkableContainer<List<E>, E> {

	private final int minSize;

	public ShrinkableList(List<Shrinkable<E>> elements, int minSize) {
		super(elements, minSize);
		this.minSize = minSize;
	}

	@Override
	Collector<E, ?, List<E>> containerCollector() {
		return Collectors.toList();
	}

	@Override
	Shrinkable<List<E>> createShrinkable(List<Shrinkable<E>> shrunkElements) {
		return new ShrinkableList<>(shrunkElements, minSize);
	}
}
