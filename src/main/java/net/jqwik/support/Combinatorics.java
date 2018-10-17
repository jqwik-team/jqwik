package net.jqwik.support;

import java.util.*;
import java.util.concurrent.atomic.*;

import static java.util.Arrays.asList;

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

	private static <T> Iterator<T> concat(List<Iterator<T>> iterators) {
		return new Iterator<T>() {

			AtomicInteger position = new AtomicInteger(0);
			Iterator<T> next = findNext();

			private Iterator<T> findNext() {
				while (!iterators.get(position.get()).hasNext()) {
					if (position.get() >= iterators.size() -1)
						return null;
					position.getAndIncrement();
				}
				return iterators.get(position.get());
			}

			@Override
			public boolean hasNext() {
				return next != null && next.hasNext();
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				T current = next.next();
				next = findNext();
				return current;
			}
		};
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

	private static class SetIterator<T> implements Iterator<Set<T>> {

		private final Iterator<List<T>> combinedListIterator;
		private final Set<Set<T>> generatedSets = new HashSet<>();
		private final int setSize;
		private Set<T> next;

		private SetIterator(Iterable<T> elementIterable, int setSize) {
			this.setSize = setSize;
			List<Iterable<T>> iterables = new ArrayList<>();
			for (int i = 0; i < setSize; i++) {
				iterables.add(elementIterable);
			}
			combinedListIterator = new CombinedIterator<>(iterables);
			next = findNext();
		}

		private Set<T> findNext() {
			while (combinedListIterator.hasNext()) {
				HashSet<T> candidate = new HashSet<>(combinedListIterator.next());
				if (candidate.size() != setSize || generatedSets.contains(candidate)) {
					continue;
				}
				generatedSets.add(candidate);
				return candidate;
			}
			return null;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Set<T> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Set<T> current = next;
			next = findNext();
			return current;
		}
	}
}

