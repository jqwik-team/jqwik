package net.jqwik.support;

import java.util.ArrayList;
import java.util.*;

import net.jqwik.support.combinatorics.*;

import static java.util.Arrays.*;

public class Combinatorics {

	@SuppressWarnings("unchecked")
	public static <T> Iterator<List<T>> combine(List<Iterable<T>> listOfIterables) {
		if (listOfIterables.isEmpty()) {
			return emptyListSingleton();
		}
		return new CombinedIterator<>(listOfIterables);
	}

	private static <T> Iterator<List<T>> emptyListSingleton() {
		return asList((List<T>) new ArrayList()).iterator();
	}

	private static <T> Iterator<Set<T>> emptySetSingleton() {
		return asList((Set<T>) new HashSet<>()).iterator();
	}

	public static <T> Iterator<List<T>> listCombinations(Iterable<T> elementIterable, int minSize, int maxSize) {
		List<Iterator<List<T>>> iterators = new ArrayList<>();
		for(int listSize = minSize; listSize <= maxSize; listSize++) {
			iterators.add(listIterator(elementIterable, listSize));
		}
		return concat(iterators);
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
		return concat(iterators);
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

	private static <T> Iterator<T> concat(List<Iterator<T>> iterators) {
		return new ConcatIterator<>(iterators);
	}

}

