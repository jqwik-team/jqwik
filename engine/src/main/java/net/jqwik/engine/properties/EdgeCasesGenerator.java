package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;

class EdgeCasesGenerator implements Iterator<List<Shrinkable<Object>>> {
	private final List<EdgeCases<Object>> edgeCases;
	private Iterator<List<Shrinkable<Object>>> iterator;
	private boolean isEmpty;

	EdgeCasesGenerator(List<EdgeCases<Object>> edgeCases) {
		this.edgeCases = edgeCases;
		reset();
	}

	public void reset() {
		if (edgeCases.size() != 1) {
			this.iterator = Collections.emptyIterator();
		} else {
			this.iterator = edgeCases.get(0).stream().map(Collections::singletonList).iterator();
		}
		this.isEmpty = !this.iterator.hasNext();
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public List<Shrinkable<Object>> next() {
		return iterator.next();
	}
}
