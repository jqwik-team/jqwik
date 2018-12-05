package net.jqwik.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;

class IterableBasedExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {

	final private Iterable<T> iterable;
	final private long maxCount;

	IterableBasedExhaustiveGenerator(Iterable<T> iterable, long maxCount) {
		this.iterable = iterable;
		this.maxCount = maxCount;
	}

	@Override
	public long maxCount() {
		return maxCount;
	}

	@Override
	public Iterator<T> iterator() {
		return iterable.iterator();
	}
}
