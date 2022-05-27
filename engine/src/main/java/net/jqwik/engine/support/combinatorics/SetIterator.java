package net.jqwik.engine.support.combinatorics;

import java.util.*;

public class SetIterator<T> implements Iterator<Set<T>> {

	private final Iterator<List<T>> combinedListIterator;
	private final Set<Set<T>> generatedSets = new LinkedHashSet<>();
	private final int setSize;
	private Set<T> next;

	public SetIterator(Iterable<T> elementIterable, int setSize) {
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
			HashSet<T> candidate = new LinkedHashSet<>(combinedListIterator.next());
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
