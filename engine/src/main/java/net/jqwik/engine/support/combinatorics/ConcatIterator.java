package net.jqwik.engine.support.combinatorics;

import java.util.*;
import java.util.concurrent.atomic.*;

public class ConcatIterator<T> implements Iterator<T> {

	private final List<? extends Iterator<? extends T>> iterators;
	private final AtomicInteger position;
	private Iterator<? extends T> next;

	public ConcatIterator(List<? extends Iterator<? extends T>> iterators) {
		this.iterators = iterators;
		position = new AtomicInteger(0);
		if (!iterators.isEmpty()) {
			next = findNext();
		}
	}

	private Iterator<? extends T> findNext() {
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
}
