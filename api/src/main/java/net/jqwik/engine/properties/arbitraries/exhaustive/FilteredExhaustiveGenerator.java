package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class FilteredExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private static final long MAX_MISSES = 10000;
	private final ExhaustiveGenerator<T> toFilter;
	private final Predicate<T> filter;

	public FilteredExhaustiveGenerator(ExhaustiveGenerator<T> toFilter, Predicate<T> filter) {
		this.toFilter = toFilter;
		this.filter = filter;
	}

	@Override
	public boolean isUnique() {
		return toFilter.isUnique();
	}

	@Override
	public long maxCount() {
		return toFilter.maxCount();
	}

	@Override
	public Iterator<T> iterator() {
		final Iterator<T> mappedIterator = toFilter.iterator();
		return new Iterator<T>() {

			T next = findNext();

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public T next() {
				if (next == null) {
					throw new NoSuchElementException();
				}
				T result = next;
				next = findNext();
				return result;
			}

			private T findNext() {
				long count = 0;
				while (true) {
					if (!mappedIterator.hasNext()) {
						return null;
					}
					T next = mappedIterator.next();
					if (filter.test(next)) {
						return next;
					} else {
						if (++count > MAX_MISSES) {
							String message =
								String.format("Filter missed more than %s times.", MAX_MISSES);
							throw new TooManyFilterMissesException(message);
						}
					}

				}
			}

		};
	}

}
