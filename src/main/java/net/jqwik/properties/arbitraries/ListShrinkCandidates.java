package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.properties.*;

public class ListShrinkCandidates<T> implements ShrinkCandidates<List<Shrinkable<T>>> {

	@Override
	public Set<List<Shrinkable<T>>> nextCandidates(List<Shrinkable<T>> toShrink) {
		if (toShrink.isEmpty()) return Collections.emptySet();
		Set<List<Shrinkable<T>>> lists = new HashSet<>();
		appendRightCut(toShrink, lists);
		appendLeftCut(toShrink, lists);
		return lists;
	}

	private void appendLeftCut(List<Shrinkable<T>> toShrink, Set<List<Shrinkable<T>>> lists) {
		List<Shrinkable<T>> leftCut = new ArrayList<>(toShrink);
		leftCut.remove(0);
		lists.add(leftCut);
	}

	private void appendRightCut(List<Shrinkable<T>> toShrink, Set<List<Shrinkable<T>>> lists) {
		List<Shrinkable<T>> rightCut = new ArrayList<>(toShrink);
		rightCut.remove(rightCut.size() - 1);
		lists.add(rightCut);
	}

	@Override
	public int distance(List<Shrinkable<T>> value) {
		int sumOfDistances = value.stream().mapToInt(Shrinkable::distance).sum();
		return value.size() + sumOfDistances;
	}
}
