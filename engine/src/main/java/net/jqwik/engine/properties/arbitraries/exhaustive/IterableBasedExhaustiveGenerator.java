package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

class IterableBasedExhaustiveGenerator<T extends @Nullable Object> implements ExhaustiveGenerator<T> {

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
