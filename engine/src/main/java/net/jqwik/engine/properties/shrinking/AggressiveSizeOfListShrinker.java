package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.engine.support.*;

public class AggressiveSizeOfListShrinker<T> {

	private final int minSize;

	public AggressiveSizeOfListShrinker(int minSize) {
		this.minSize = minSize;
	}

	public Stream<List<T>> shrink(List<T> toShrink) {
		if (toShrink.size() <= minSize) {
			return Stream.empty();
		}
		return JqwikStreamSupport.concat(
			cutsToMinsize(toShrink),
			cutsToMinsizePlus1(toShrink),
			cutInHalves(toShrink)
		).filter(l -> l.size() >= minSize);
	}

	public Stream<List<T>> cutsToMinsize(List<T> toShrink) {
		Set<List<T>> lists = new LinkedHashSet<>();
		appendLeftCut(toShrink, lists, minSize);
		appendRightCut(toShrink, lists, minSize);
		return lists.stream();
	}

	public Stream<List<T>> cutsToMinsizePlus1(List<T> toShrink) {
		if (toShrink.size() <= minSize + 1) {
			return Stream.empty();
		}
		Set<List<T>> lists = new LinkedHashSet<>();
		appendLeftCut(toShrink, lists, minSize + 1);
		appendRightCut(toShrink, lists, minSize + 1);
		return lists.stream();
	}

	public Stream<List<T>> cutInHalves(List<T> toShrink) {
		int halfSize = toShrink.size() / 2;
		if (halfSize < minSize) {
			return Stream.empty();
		}
		Set<List<T>> lists = new LinkedHashSet<>();
		appendLeftCut(toShrink, lists, halfSize);
		appendRightCut(toShrink, lists, toShrink.size() - halfSize);
		return lists.stream();
	}

	private void appendLeftCut(List<T> toShrink, Set<List<T>> lists, int elementsToKeep) {
		List<T> cut = new ArrayList<>(toShrink);
		lists.add(cut.subList(0, elementsToKeep));
	}

	private void appendRightCut(List<T> toShrink, Set<List<T>> lists, int elementsToKeep) {
		List<T> cut = new ArrayList<>(toShrink);
		int elementsToCut = toShrink.size() - elementsToKeep;
		lists.add(cut.subList(elementsToCut, toShrink.size()));
	}

}
