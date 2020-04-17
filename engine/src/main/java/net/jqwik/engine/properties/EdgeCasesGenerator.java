package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

class EdgeCasesGenerator implements Iterator<List<Shrinkable<Object>>> {
	private final List<EdgeCases<Object>> edgeCases;
	private Iterator<List<Shrinkable<Object>>> iterator;
	private boolean isEmpty;

	EdgeCasesGenerator(List<EdgeCases<Object>> edgeCases) {
		this.edgeCases = edgeCases;
		reset();
	}

	public void reset() {
		if (edgeCases.isEmpty()) {
			this.iterator = Collections.emptyIterator();
		} else {
			this.iterator = createIterator();
		}
		this.isEmpty = !this.iterator.hasNext();
	}

	protected Iterator<List<Shrinkable<Object>>> createIterator() {
		List<Iterable<Shrinkable<Object>>> iterables =
			edgeCases
				.stream()
				.map(edge -> (Iterable<Shrinkable<Object>>) edge)
				.collect(Collectors.toList());
		return Combinatorics.combine(iterables);
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
