package net.jqwik.support;

import java.util.*;

public class Combinatorics {
	public static Iterator<List> combine(List<Iterable> listOfIterables) {
		return new CombinedIterator(listOfIterables);
	}

	private static class CombinedIterator<T> implements Iterator<List<T>> {

		private final Iterator<T> first;
		private final ArrayList<Iterable<T>> rest;
		private Iterator<List<T>> next;

		private static <U> Iterator<List<U>> emptyListSingleton() {
			return Arrays.asList((List<U>) new ArrayList<U>()).iterator();
		}

		private T current = null;

		private CombinedIterator(List<Iterable<T>> iterables) {
			this.rest = new ArrayList<>(iterables);
			this.first = this.rest.remove(0).iterator();
			this.next = restIterator();
		}

		private Iterator<List<T>> restIterator() {
			return this.rest.isEmpty()
					   ? emptyListSingleton()
					   : new CombinedIterator<T>(this.rest);
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

		private List<T> prepend(T head, List<T> tail) {
			List<T> rest = new ArrayList<>(tail);
			rest.add(0, head);
			return rest;
		}
	}
}

