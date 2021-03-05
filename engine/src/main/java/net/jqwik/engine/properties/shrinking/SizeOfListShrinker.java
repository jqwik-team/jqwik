package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.engine.support.*;

public class SizeOfListShrinker<T> {

	private final int minSize;

	public SizeOfListShrinker(int minSize) {
		this.minSize = minSize;
	}

	public Stream<List<T>> shrink(List<T> toShrink) {
		if (toShrink.size() <= minSize)
			return Stream.empty();
		return JqwikStreamSupport.concat(
			emptyList(),
			cuts(toShrink)
		).filter(l -> l.size() >= minSize);
	}

	private Stream<List<T>> emptyList() {
		if (minSize == 0) {
			return Stream.of(new ArrayList<>());
		} else {
			return Stream.empty();
		}
	}

	public Stream<List<T>> cuts(List<T> toShrink) {
		Set<List<T>> lists = new HashSet<>();
		appendRightCuts(toShrink, lists);
		appendLeftCuts(toShrink, lists);
		return lists.stream();
	}

	private void appendLeftCuts(List<T> toShrink, Set<List<T>> lists) {
		int elementsToCut = calculateElementsToCut(toShrink.size());
		appendLeftCut(toShrink, lists, elementsToCut);
		if (elementsToCut != 1) {
			appendLeftCut(toShrink, lists, 1);
		}
	}

	private void appendLeftCut(List<T> toShrink, Set<List<T>> lists, int elementsToCut) {
		List<T> leftCut = new ArrayList<>(toShrink);
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

	private void appendRightCuts(List<T> toShrink, Set<List<T>> lists) {
		int elementsToCut = calculateElementsToCut(toShrink.size());
		appendRightCut(toShrink, lists, elementsToCut);
		if (elementsToCut != 1) {
			appendRightCut(toShrink, lists, 1);
		}
	}

	private void appendRightCut(List<T> toShrink, Set<List<T>> lists, int elementsToCut) {
		List<T> rightCut = new ArrayList<>(toShrink);
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
