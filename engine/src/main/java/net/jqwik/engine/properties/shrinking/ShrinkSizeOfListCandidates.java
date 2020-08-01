package net.jqwik.engine.properties.shrinking;

import java.util.*;

import net.jqwik.api.*;

public class ShrinkSizeOfListCandidates<T> implements ShrinkingCandidates<List<Shrinkable<T>>> {

	private final int minSize;

	public ShrinkSizeOfListCandidates(int minSize) {
		this.minSize = minSize;
	}

	@Override
	public Set<List<Shrinkable<T>>> candidatesFor(List<Shrinkable<T>> toShrink) {
		if (toShrink.size() <= minSize)
			return Collections.emptySet();
		Set<List<Shrinkable<T>>> lists = new HashSet<>();
		appendRightCuts(toShrink, lists);
		appendLeftCuts(toShrink, lists);
		return lists;
	}

	private void appendLeftCuts(List<Shrinkable<T>> toShrink, Set<List<Shrinkable<T>>> lists) {
		int elementsToCut = calculateElementsToCut(toShrink.size());
		appendLeftCut(toShrink, lists, elementsToCut);
		if (elementsToCut != 1) {
			appendLeftCut(toShrink, lists, 1);
		}
	}

	private void appendLeftCut(List<Shrinkable<T>> toShrink, Set<List<Shrinkable<T>>> lists, int elementsToCut) {
		List<Shrinkable<T>> leftCut = new ArrayList<>(toShrink);
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

	private void cutFromLeft(List<Shrinkable<T>> leftCut, int elementsToCut) {
		if (elementsToCut == 0)
			return;
		leftCut.remove(0);
		cutFromLeft(leftCut, --elementsToCut);
	}

	private void appendRightCuts(List<Shrinkable<T>> toShrink, Set<List<Shrinkable<T>>> lists) {
		int elementsToCut = calculateElementsToCut(toShrink.size());
		appendRightCut(toShrink, lists, elementsToCut);
		if (elementsToCut != 1) {
			appendRightCut(toShrink, lists, 1);
		}
	}

	private void appendRightCut(List<Shrinkable<T>> toShrink, Set<List<Shrinkable<T>>> lists, int elementsToCut) {
		List<Shrinkable<T>> rightCut = new ArrayList<>(toShrink);
		cutFromRight(rightCut, elementsToCut);
		lists.add(rightCut);
	}

	private void cutFromRight(List<Shrinkable<T>> rightCut, int elementsToCut) {
		if (elementsToCut == 0)
			return;
		rightCut.remove(rightCut.size() - 1);
		cutFromRight(rightCut, --elementsToCut);
	}
}
