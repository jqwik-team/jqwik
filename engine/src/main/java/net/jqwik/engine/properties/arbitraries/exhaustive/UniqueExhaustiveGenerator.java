package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.concurrent.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

public class UniqueExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private final ExhaustiveGenerator<T> base;

	public UniqueExhaustiveGenerator(ExhaustiveGenerator<T> base) {
		this.base = base;
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
				return MaxTriesLoop.loop(
					() -> true,
					next -> {
						if (!mappedIterator.hasNext()) {
							return Tuple.of(true, null);
						}
						next = mappedIterator.next();
						if (!usedValues.contains(next)) {
							usedValues.add(next);
							return Tuple.of(true, next);
						}
						return Tuple.of(false, next);
					},
					maxMisses -> {
						String message =
							String.format("Uniqueness filter missed more than %s times.", maxMisses);
						return new TooManyFilterMissesException(message);
					}
				);
			}
		};
	}

}
