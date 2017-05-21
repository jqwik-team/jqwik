package net.jqwik.newArbitraries;

import java.util.*;

public class NListShrinker<T> implements NShrinker<List<NShrinkable<T>>> {

	@Override
	public Set<List<NShrinkable<T>>> shrink(List<NShrinkable<T>> toShrink) {
		if (toShrink.isEmpty()) return Collections.emptySet();
		Set<List<NShrinkable<T>>> lists = new HashSet<>();
		List<NShrinkable<T>> rightCut = new ArrayList<>(toShrink);
		rightCut.remove(rightCut.size() - 1);
		lists.add(rightCut);
		List<NShrinkable<T>> leftCut = new ArrayList<>(toShrink);
		leftCut.remove(0);
		lists.add(leftCut);
		return lists;
	}

	@Override
	public int distance(List<NShrinkable<T>> value) {
		int sumOfDistances = value.stream().mapToInt(NShrinkable::distance).sum();
		return value.size() + sumOfDistances;
	}
}
