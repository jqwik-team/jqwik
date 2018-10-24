package net.jqwik.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.concurrent.*;

import net.jqwik.*;
import net.jqwik.api.*;

public class UniqueExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private static final long MAX_MISSES = 10000;
	private final ExhaustiveGenerator<T> base;

	public UniqueExhaustiveGenerator(ExhaustiveGenerator<T> base) {
		this.base = base;
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	@Override
	public long maxCount() {
		return base.maxCount();
	}

	@Override
	public Iterator<T> iterator() {
		final Iterator<T> mappedIterator = base.iterator();
		final Set<T> usedValues = ConcurrentHashMap.newKeySet();

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
					if (!usedValues.contains(next)) {
						usedValues.add(next);
						return next;
					} else {
						if (++count > MAX_MISSES) {
							String message =
								String.format("Uniqueness filter missed more than %s times.", MAX_MISSES);
							throw new JqwikException(message);
						}
					}

				}
			}

		};
	}

}
