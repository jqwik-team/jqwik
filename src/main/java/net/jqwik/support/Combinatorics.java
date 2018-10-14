package net.jqwik.support;

import java.util.*;

public class Combinatorics {
	public static Iterator<List> combine(List<Iterable> listOfIterables) {
		return new CombinedIterator(listOfIterables);
	}

	private static class CombinedIterator<T> implements Iterator<List<T>> {

		private final Iterator<T> iterator;

		public CombinedIterator(List<Iterable<T>> iterables) {
			this.iterator = iterables.get(0).iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public List<T> next() {
			return Collections.singletonList(iterator.next());
		}
	}
}
