package net.jqwik.support;

import java.util.*;

public class Combinatorics {

	@SuppressWarnings("unchecked")
	public static Iterator<List> combine(List<Iterable> listOfIterables) {
		if (listOfIterables.isEmpty()) {
			return emptyListSingleton();
		}
		return new CombinedIterator(listOfIterables);
	}

	private static Iterator<List> emptyListSingleton() {
		return Arrays.asList((List) new ArrayList()).iterator();
	}

	public static <T> Iterator<List<T>> combineList(Iterable<T> elementIterable, int minSize, int maxSize) {
		return new ArrayList<List<T>>().iterator();
	}

	private static class CombinedIterator implements Iterator<List> {

		private final Iterator first;
		private final ArrayList<Iterable> rest;
		private Iterator<List> next;

		private Object current = null;

		private CombinedIterator(List<Iterable> iterables) {
			this.rest = new ArrayList<>(iterables);
			this.first = this.rest.remove(0).iterator();
			this.next = restIterator();
		}

		private Iterator<List> restIterator() {
			return this.rest.isEmpty()
					   ? emptyListSingleton()
					   : new CombinedIterator(this.rest);
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
		public List next() {
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
		private List prepend(Object head, List tail) {
			List rest = new ArrayList(tail);
			rest.add(0, head);
			return rest;
		}
	}
}

