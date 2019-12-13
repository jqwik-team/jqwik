package net.jqwik.engine.support.combinatorics;

import java.util.ArrayList;
import java.util.*;

import static java.util.Arrays.*;

public class CombinedIterator<T> implements Iterator<List<T>> {

	private final Iterator first;
	private final ArrayList<Iterable<T>> rest;
	private Iterator<List<T>> next;

	private Object current = null;

	// This must be tracked because there can be null values
	private boolean currentIsSet = false;

	public CombinedIterator(List<Iterable<T>> iterables) {
		this.rest = new ArrayList<>(iterables);
		this.first = this.rest.remove(0).iterator();
		this.next = restIterator();
	}

	private Iterator<List<T>> restIterator() {
		return this.rest.isEmpty()
				   ? emptyListIterator()
				   : new CombinedIterator<>(this.rest);
	}

	private Iterator<List<T>> emptyListIterator() {
		return asList((List<T>) new ArrayList()).iterator();
	}

	@Override
	public boolean hasNext() {
		if (currentIsSet) {
			return next.hasNext() || first.hasNext();
		} else {
			return next.hasNext() && first.hasNext();
		}
	}

	@Override
	public List<T> next() {
		if (next.hasNext()) {
			if (!currentIsSet) {
				current = first.next();
				currentIsSet = true;
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
