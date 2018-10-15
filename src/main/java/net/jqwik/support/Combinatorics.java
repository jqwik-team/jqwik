package net.jqwik.support;

import java.util.*;

public class Combinatorics {
	public static Iterator<List> combine(List<Iterable> listOfIterables) {
		return new CombinedIterator(listOfIterables);
	}

	private static class CombinedIterator<T> implements Iterator<List<T>> {

		private final Iterator<T> first;
		private final ArrayList<Iterable<T>> iterables;
		private Iterator<List<T>> next = null;
		private T current = null;

		private CombinedIterator(List<Iterable<T>> iterables) {
			this.iterables = new ArrayList<>(iterables);
			this.first = this.iterables.remove(0).iterator();
			if (!this.iterables.isEmpty()) {
				this.next = new CombinedIterator<>(this.iterables);
			}
		}

		@Override
		public boolean hasNext() {
			if (next != null) {
				if (current == null) {
					return next.hasNext() && first.hasNext();
				} else {
					return next.hasNext() || first.hasNext();
				}
			} else {
				return first.hasNext();
			}
		}

		@Override
		public List<T> next() {
			if (next != null) {
				if (next.hasNext()) {
					if (current == null) {
						current = first.next();
					}
					List<T> rest = new ArrayList<>(next.next());
					rest.add(0, current);
					return rest;
				} else {
					current = first.next();
					next = new CombinedIterator<>(iterables);
					List<T> rest = new ArrayList<>(next.next());
					rest.add(0, current);
					return rest;
				}
			}  else {
				current = first.next();
				return Collections.singletonList(current);
			}
		}
	}
}

