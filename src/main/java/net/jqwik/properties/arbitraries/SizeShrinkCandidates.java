package net.jqwik.properties.arbitraries;

import java.util.*;

class SizeShrinkCandidates implements ShrinkCandidates<Integer> {
	@Override
	public Set<Integer> nextCandidates(Integer value) {
		if (value == 0) {
			return Collections.emptySet();
		}
		return Collections.singleton(value - 1);
	}

	@Override
	public int distance(Integer value) {
		return value;
	}
}
