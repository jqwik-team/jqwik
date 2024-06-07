package net.jqwik.engine.support.combinatorics;

import org.jspecify.annotations.*;

import java.util.*;

// Taken and adapted from https://codereview.stackexchange.com/questions/119969/
public class PermutationIterator<T extends @Nullable Object> implements Iterator<List<T>> {
	private final List<T> values = new ArrayList<>();
	private @Nullable List<T> next;
	private int @Nullable [] indices;

	public PermutationIterator(List<? extends T> values) {
		this.values.addAll(values);
		initializeIndices(values);

		if (values.isEmpty()) {
			next = null;
		} else {
			next = new ArrayList<>(this.values);
		}
	}

	private void initializeIndices(List<? extends T> values) {
		this.indices = new int[values.size()];
		for (int i = 0; i < indices.length; ++i) {
			indices[i] = i;
		}
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public List<T> next() {
		if (next == null) {
			throw new NoSuchElementException();
		}

		List<T> current = next;
		this.next = findNext();
		return current;
	}

	private List<T> findNext() {
		int index = nextIndexToChange();
		if (index == -1) {
			// No more new permutations.
			return null;
		}

		generateNextIndices(index);

		return current();
	}

	private int nextIndexToChange() {
		int i = indices.length - 2;
		while (i >= 0 && indices[i] > indices[i + 1]) {
			i--;
		}
		return i;
	}

	private void generateNextIndices(int i) {
		int j = i + 1;
		int min = indices[j];
		int minIndex = j;

		while (j < indices.length) {
			if (indices[i] < indices[j] && indices[j] < min) {
				min = indices[j];
				minIndex = j;
			}
			j++;
		}

		swap(indices, i++, minIndex);
		j = indices.length - 1;

		while (i < j) {
			swap(indices, i++, j--);
		}
	}

	private List<T> current() {
		List<T> newPermutation = new ArrayList<>(indices.length);
		for (int i : indices) {
			newPermutation.add(values.get(i));
		}
		return newPermutation;
	}

	private static void swap(int[] array, int a, int b) {
		int tmp = array[a];
		array[a] = array[b];
		array[b] = tmp;
	}

}
