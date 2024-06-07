package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static net.jqwik.engine.support.JqwikExceptionSupport.*;

public class IgnoreExceptionExhaustiveGenerator<T extends @Nullable Object> implements ExhaustiveGenerator<T> {
	private final ExhaustiveGenerator<T> toFilter;
	private final Class<? extends Throwable>[] exceptionTypes;
	private final int maxThrows;

	public IgnoreExceptionExhaustiveGenerator(ExhaustiveGenerator<T> toFilter, Class<? extends Throwable>[] exceptionTypes, int maxThrows) {
		this.toFilter = toFilter;
		this.exceptionTypes = exceptionTypes;
		this.maxThrows = maxThrows;
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
				for (int i = 0; i < maxThrows; i++) {
					if (!mappedIterator.hasNext()) {
						return null;
					}
					try {
						return mappedIterator.next();
					} catch (Throwable throwable) {
						if (isInstanceOfAny(throwable, exceptionTypes)) {
							continue;
						}
						throw throwable;
					}
				}
				String message = String.format("Filter missed more than %s times.", maxThrows);
				throw new TooManyFilterMissesException(message);
			}

		};
	}

}
