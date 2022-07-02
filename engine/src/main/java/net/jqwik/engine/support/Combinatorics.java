package net.jqwik.engine.support;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.support.combinatorics.*;

import static java.util.Arrays.*;

public class Combinatorics {

	public static <T> Iterator<List<T>> combine(List<Iterable<T>> listOfIterables) {
		if (listOfIterables.isEmpty()) {
			return emptyListSingleton();
		}
		return new CombinedIterator<>(listOfIterables);
	}

	@SuppressWarnings("unchecked")
	private static <T> Iterator<List<T>> emptyListSingleton() {
		return asList((List<T>) new ArrayList<>()).iterator();
	}

	@SuppressWarnings("unchecked")
	private static <T> Iterator<Set<T>> emptySetSingleton() {
		return asList((Set<T>) new LinkedHashSet<>()).iterator();
	}

	public static <T> Iterator<List<T>> listCombinations(Iterable<T> elementIterable, int minSize, int maxSize) {
		List<Iterator<List<T>>> iterators = new ArrayList<>();
		for(int listSize = minSize; listSize <= maxSize; listSize++) {
			iterators.add(listIterator(elementIterable, listSize));
		}
		return concatIterators(iterators);
	}

	private static <T> Iterator<List<T>> listIterator(Iterable<T> elementIterable, int listSize) {
		List<Iterable<T>> listOfIterables = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			listOfIterables.add(elementIterable);
		}
		return combine(listOfIterables);
	}

	public static <T> Iterator<Set<T>> setCombinations(Iterable<T> elementIterable, int minSize, int maxSize) {
		List<Iterator<Set<T>>> iterators = new ArrayList<>();
		for(int setSize = minSize; setSize <= maxSize; setSize++) {
			Iterator<Set<T>> setIterator = setIterator(elementIterable, setSize);
			iterators.add(setIterator);
		}
		return concatIterators(iterators);
	}

	private static <T> Iterator<Set<T>> setIterator(Iterable<T> elementIterable, int setSize) {
		if (setSize == 0) {
			return emptySetSingleton();
		}
		return new SetIterator<>(elementIterable, setSize);
	}

	public static <T> Iterator<List<T>> listPermutations(List<T> values) {
		if (values.isEmpty()) {
			return emptyListSingleton();
		}
		return new PermutationIterator<>(values);
	}

	public static <T> Iterator<T> concat(List<Iterable<T>> iterables) {
		List<Iterator<T>> iterators = iterables.stream().map(Iterable::iterator).collect(Collectors.toList());
		return new ConcatIterator<>(iterators);
	}

	private static <T> Iterator<T> concatIterators(List<Iterator<T>> iterators) {
		return new ConcatIterator<>(iterators);
	}

	public static Stream<Tuple2<Integer, Integer>> distinctPairs(int maxExclusive) {
		if (maxExclusive < 2) {
			return Stream.empty();
		}
		List<Tuple2<Integer, Integer>> pairs = new ArrayList<>();
		for (int i = 0; i < maxExclusive; i++) {
			for (int j = i + 1; j < maxExclusive; j++) {
				if (i != j) {
					pairs.add(Tuple.of(i, j));
				}
			}
		}
		return pairs.stream();
	}
}

