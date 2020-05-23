package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

public class FilteredExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private final ExhaustiveGenerator<T> toFilter;
	private final Predicate<T> filter;

	public FilteredExhaustiveGenerator(ExhaustiveGenerator<T> toFilter, Predicate<T> filter) {
		this.toFilter = toFilter;
		this.filter = filter;
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
				return MaxTriesLoop.loop(
					() -> true,
					next -> {
						if (!mappedIterator.hasNext()) {
							return Tuple.of(true, null);
						}
						next = mappedIterator.next();
						if ((filter.test(next))) {
							return Tuple.of(true, next);
						}
						return Tuple.of(false, next);
					},
					maxMisses -> {
						String message =
							String.format("Filter missed more than %s times.", maxMisses);
						return new TooManyFilterMissesException(message);
					}
				);
			}

		};
	}

}
