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
		int elementsToCut = calculateElementsToCut(toShrink.size());
		cutFromLeft(leftCut, elementsToCut);
		lists.add(leftCut);
	}

	private int calculateElementsToCut(int listSize) {
		if (listSize <= 10)
			return 1;
		if (listSize < 20)
			return listSize - 9;
		return listSize / 2;
	}

	private void cutFromLeft(List<Shrinkable<T>> leftCut, int elementsToCut) {
		if (elementsToCut == 0)
			return;
		leftCut.remove(0);
		cutFromLeft(leftCut, --elementsToCut);
	}

	private void appendRightCut(List<Shrinkable<T>> toShrink, Set<List<Shrinkable<T>>> lists) {
		List<Shrinkable<T>> rightCut = new ArrayList<>(toShrink);
		int elementsToCut = calculateElementsToCut(toShrink.size());
		cutFromRight(rightCut, elementsToCut);
		lists.add(rightCut);
	}

	private void cutFromRight(List<Shrinkable<T>> rightCut, int elementsToCut) {
		if (elementsToCut == 0)
			return;
		rightCut.remove(rightCut.size() - 1);
		cutFromRight(rightCut, --elementsToCut);
	}

	@Override
	public int distance(List<Shrinkable<T>> value) {
		// The algorithm is more involved because the distance may never never never overflow
		int sumOfDistances = 0;
		for (Shrinkable<T> tShrinkable : value) {
			int distance = tShrinkable.distance();
			long newDistance = (long) sumOfDistances + (long) distance;
			if (newDistance >= Integer.MAX_VALUE)
				return	Integer.MAX_VALUE;
			sumOfDistances = (int) newDistance;
		}
		return value.size() + sumOfDistances;
	}
}
