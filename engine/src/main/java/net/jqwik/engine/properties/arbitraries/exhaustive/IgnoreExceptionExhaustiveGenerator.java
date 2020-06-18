package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

public class IgnoreExceptionExhaustiveGenerator<T> implements ExhaustiveGenerator<T> {
	private final ExhaustiveGenerator<T> toFilter;
	private final Class<? extends Throwable> exceptionType;

	public IgnoreExceptionExhaustiveGenerator(ExhaustiveGenerator<T> toFilter, Class<? extends Throwable> exceptionType) {
		this.toFilter = toFilter;
		this.exceptionType = exceptionType;
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
						try {
							next = mappedIterator.next();
							return Tuple.of(true, next);
						} catch (Throwable throwable) {
							if (exceptionType.isAssignableFrom(throwable.getClass())) {
								return Tuple.of(false, next);
							}
							throw throwable;
						}
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
