package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class FilteredExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private final ExhaustiveGenerator<T> toFilter;
	private final Predicate<? super T> filter;
	private int maxMisses;

	public FilteredExhaustiveGenerator(ExhaustiveGenerator<T> toFilter, Predicate<? super T> filter, int maxMisses) {
		this.toFilter = toFilter;
		this.filter = filter;
		this.maxMisses = maxMisses;
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
				for (int i = 0; i < maxMisses; i++) {
					if (!mappedIterator.hasNext()) {
						return null;
					}
					T value = mappedIterator.next();
					if (filter.test(value)) {
						return value;
					}
				}
				String message = String.format("Filter missed more than %s times.", maxMisses);
				throw new TooManyFilterMissesException(message);
			}

		};
	}

}
