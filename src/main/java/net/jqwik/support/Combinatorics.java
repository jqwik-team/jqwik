package net.jqwik.support;

import java.util.*;

public class Combinatorics {

	@SuppressWarnings("unchecked")
	public static <T> Iterator<List<T>> combine(List<Iterable<T>> listOfIterables) {
		if (listOfIterables.isEmpty()) {
			return emptyListSingleton();
		}
		return new CombinedIterator<>(listOfIterables);
	}

	private static <T> Iterator<List<T>> emptyListSingleton() {
		return Arrays.asList((List<T>) new ArrayList()).iterator();
	}

	public static <T> Iterator<List<T>> listCombinations(Iterable<T> elementIterable, int minSize, int maxSize) {
		List<Iterable<T>> listOfIterables = new ArrayList<>();
		for (int i = 0; i < maxSize; i++) {
			listOfIterables.add(elementIterable);
		}
		return combine(listOfIterables);
	}

	private static class CombinedIterator<T> implements Iterator<List<T>> {

		private final Iterator first;
		private final ArrayList<Iterable<T>> rest;
		private Iterator<List<T>> next;

		private Object current = null;

		private CombinedIterator(List<Iterable<T>> iterables) {
			this.rest = new ArrayList<>(iterables);
			this.first = this.rest.remove(0).iterator();
			this.next = restIterator();
		}

		private Iterator<List<T>> restIterator() {
			return this.rest.isEmpty()
					   ? emptyListSingleton()
					   : new CombinedIterator<>(this.rest);
		}

		@Override
		public boolean hasNext() {
			if (current == null) {
				return next.hasNext() && first.hasNext();
			} else {
				return next.hasNext() || first.hasNext();
			}
		}

		@Override
		public List<T> next() {
			if (next.hasNext()) {
				if (current == null) {
					current = first.next();
				}
			} else {
				current = first.next();
				this.next = restIterator();
			}
			return prepend(current, next.next());
		}

		@SuppressWarnings("unchecked")
		private List<T> prepend(Object head, List tail) {
			List rest = new ArrayList(tail);
			rest.add(0, head);
			return rest;
		}
	}
}

