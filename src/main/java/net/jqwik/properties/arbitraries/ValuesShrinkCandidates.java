package net.jqwik.properties.arbitraries;

import java.util.*;

class ValuesShrinkCandidates<T> implements ShrinkCandidates<T> {

	private final List<T> values;

	ValuesShrinkCandidates(List<T> values) {
		this.values = values;
	}

	@Override
	public Set<T> nextCandidates(T value) {
		int index = values.indexOf(value);
		if (index == 0) {
			return Collections.emptySet();
		}
		return new HashSet<>(values.subList(0, index));
	}

	@Override
	public int distance(T value) {
		return values.indexOf(value);
	}
}
