package net.jqwik.engine.properties.shrinking;

import java.util.*;

public class ListShrinkingCandidates<T> implements ShrinkingCandidates<List<T>> {

	private final int minSize;

	public ListShrinkingCandidates(int minSize) {
		this.minSize = minSize;
	}

	@Override
	public Set<List<T>> candidatesFor(List<T> toShrink) {
		if (toShrink.size() <= minSize)
			return Collections.emptySet();
		Set<List<T>> lists = new HashSet<>();
		appendRightCut(toShrink, lists);
		appendLeftCut(toShrink, lists);
		return lists;
	}

	private void appendLeftCut(List<T> toShrink, Set<List<T>> lists) {
		List<T> leftCut = new ArrayList<>(toShrink);
		int elementsToCut = calculateElementsToCut(toShrink.size());
		cutFromLeft(leftCut, elementsToCut);
		lists.add(leftCut);
	}

	private int calculateElementsToCut(int listSize) {
		int toCut = rawElementsToCut(listSize);
		return Math.min(toCut, listSize - minSize);
	}

	private int rawElementsToCut(int listSize) {
		// TODO: Improve cut size. Those values are purely guesses.
		// Maybe use integer shrinking to determine target size.
		if (listSize <= 10)
			return 1;
		if (listSize < 20)
			return listSize - 9;
		return listSize / 2;
	}

	private void cutFromLeft(List<T> leftCut, int elementsToCut) {
		if (elementsToCut == 0)
			return;
		leftCut.remove(0);
		cutFromLeft(leftCut, --elementsToCut);
	}

	private void appendRightCut(List<T> toShrink, Set<List<T>> lists) {
		List<T> rightCut = new ArrayList<>(toShrink);
		int elementsToCut = calculateElementsToCut(toShrink.size());
		cutFromRight(rightCut, elementsToCut);
		lists.add(rightCut);
	}

	private void cutFromRight(List<T> rightCut, int elementsToCut) {
		if (elementsToCut == 0)
			return;
		rightCut.remove(rightCut.size() - 1);
		cutFromRight(rightCut, --elementsToCut);
	}
}
