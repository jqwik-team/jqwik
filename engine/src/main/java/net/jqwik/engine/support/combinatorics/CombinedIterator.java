package net.jqwik.engine.support.combinatorics;

import java.util.*;
import java.util.stream.*;

public class CombinedIterator<T> implements Iterator<List<T>> {
	private final List<? extends Iterable<? extends T>> iterables;
	private final List<Iterator<? extends T>> iterators;
	private final List<T> elements;
	private final boolean isEmpty;
	private int position = -1;

	public CombinedIterator(List<? extends Iterable<? extends T>> iterables) {
		this.iterables = iterables;
		elements = new ArrayList<>(Collections.nCopies(iterables.size(), null));
		iterators = iterables.stream().map(Iterable::iterator).collect(Collectors.toCollection(ArrayList::new));
		isEmpty = iterables.isEmpty() || !iterators.stream().allMatch(Iterator::hasNext);
	}

	@Override
	public boolean hasNext() {
		if (isEmpty) {
			return false;
		}
		return position == -1 || nextAvailablePosition() != -1;
	}

	@Override
	public List<T> next() {
		if (isEmpty) {
			throw new NoSuchElementException();
		}
		if (position == -1) {
			// The first initialization of the values
			resetValuesFrom(0);
		} else {
			Iterator<? extends T> it = iterators.get(position);
			if (it.hasNext()) {
				// Just advance the current iterator
				elements.set(position, it.next());
			} else {
				// Advance the next iterator, and reset (nextPosition, size)
				position--;
				int nextPosition = nextAvailablePosition();
				if (nextPosition == -1) {
					throw new NoSuchElementException();
				}
				elements.set(nextPosition, iterators.get(nextPosition).next());
				resetValuesFrom(nextPosition + 1);
			}
		}
		return new ArrayList<>(elements);
	}

	private void resetValuesFrom(int startPosition) {
		List<Iterator<? extends T>> iterators = this.iterators;
		List<? extends Iterable<? extends T>> iterables = this.iterables;
		List<T> elements = this.elements;
		// In the initial reset, we can reuse the existing iterators
		// It might slightly optimize the behavior, and it supports Stream#iterator which can't be executed twice
		boolean initialReset = position == -1;
		for (int i = startPosition; i < iterables.size(); i++) {
			Iterator<? extends T> newIt = initialReset ? iterators.get(i) : iterables.get(i).iterator();
			iterators.set(i, newIt);
			elements.set(i, newIt.next());
		}
		position = iterables.size() - 1;
	}

	private int nextAvailablePosition() {
		List<Iterator<? extends T>> iterators = this.iterators;
		for (int i = position; i >= 0; i--) {
			if (iterators.get(i).hasNext()) {
				return i;
			}
		}
		return -1;
	}
}
