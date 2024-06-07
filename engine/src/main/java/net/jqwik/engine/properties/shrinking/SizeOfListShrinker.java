package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;

public class SizeOfListShrinker<T extends @Nullable Object> {

	private final int minSize;

	public SizeOfListShrinker(int minSize) {
		this.minSize = minSize;
	}

	public Stream<List<T>> shrink(List<? extends T> toShrink) {
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

	public Stream<List<T>> cuts(List<? extends T> toShrink) {
		Set<List<T>> lists = new LinkedHashSet<>();
		appendRightCuts(toShrink, lists);
		appendLeftCuts(toShrink, lists);
		return lists.stream();
	}

	private void appendLeftCuts(List<? extends T> toShrink, Set<List<T>> lists) {
		int elementsToCut = calculateElementsToCut(toShrink.size());
		appendLeftCut(toShrink, lists, elementsToCut);
		if (elementsToCut != 1) {
			appendLeftCut(toShrink, lists, 1);
		}
	}

	private void appendLeftCut(List<? extends T> toShrink, Set<List<T>> lists, int elementsToCut) {
		lists.add(new ArrayList<>(cutFromLeft(toShrink, elementsToCut)));
	}

	private List<? extends T> cutFromLeft(List<? extends T> toShrink, int elementsToCut) {
		return toShrink.subList(elementsToCut, toShrink.size());
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

	private void appendRightCuts(List<? extends T> toShrink, Set<List<T>> lists) {
		int elementsToCut = calculateElementsToCut(toShrink.size());
		appendRightCut(toShrink, lists, elementsToCut);
		if (elementsToCut != 1) {
			appendRightCut(toShrink, lists, 1);
		}
	}

	private void appendRightCut(List<? extends T> toShrink, Set<List<T>> lists, int elementsToCut) {
		lists.add(new ArrayList<>(cutFromRight(toShrink, elementsToCut)));
	}

	private List<? extends T> cutFromRight(List<? extends T> toShrink, int elementsToCut) {
		return toShrink.subList(0, toShrink.size() - elementsToCut);
	}
}
