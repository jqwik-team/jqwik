package net.jqwik.properties.newShrinking;

import java.util.*;

public class NListShrinkable<T> implements NShrinkable<List<T>> {
	public NListShrinkable(List<NShrinkable<T>> elementShrinkables) {
	}

	@Override
	public List<T> value() {
		return null;
	}

	@Override
	public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
		return null;
	}

	@Override
	public ShrinkingDistance distance() {
		return null;
	}
}
