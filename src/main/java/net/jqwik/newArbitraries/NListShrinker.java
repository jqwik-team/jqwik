package net.jqwik.newArbitraries;

import java.util.*;

public class NListShrinker<T> implements NShrinker<List<NShrinkable<T>>> {

	@Override
	public Set<List<NShrinkable<T>>> nextShrinkingCandidates(List<NShrinkable<T>> toShrink) {
		if (toShrink.isEmpty()) return Collections.emptySet();
		Set<List<NShrinkable<T>>> lists = new HashSet<>();
		appendRightCut(toShrink, lists);
		appendLeftCut(toShrink, lists);
		return lists;
	}

	private void appendLeftCut(List<NShrinkable<T>> toShrink, Set<List<NShrinkable<T>>> lists) {
		List<NShrinkable<T>> leftCut = new ArrayList<>(toShrink);
		leftCut.remove(0);
		lists.add(leftCut);
	}

	private void appendRightCut(List<NShrinkable<T>> toShrink, Set<List<NShrinkable<T>>> lists) {
		List<NShrinkable<T>> rightCut = new ArrayList<>(toShrink);
		rightCut.remove(rightCut.size() - 1);
		lists.add(rightCut);
	}

	@Override
	public int distance(List<NShrinkable<T>> value) {
		int sumOfDistances = value.stream().mapToInt(NShrinkable::distance).sum();
		return value.size() + sumOfDistances;
	}
}
