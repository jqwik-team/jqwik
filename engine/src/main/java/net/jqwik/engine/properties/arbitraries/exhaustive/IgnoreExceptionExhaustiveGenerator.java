package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;

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
				for (int i = 0; i < 10000; i++) {
					if (!mappedIterator.hasNext()) {
						return null;
					}
					try {
						return mappedIterator.next();
					} catch (Throwable throwable) {
						if (exceptionType.isInstance(throwable)) {
							continue;
						}
						throw throwable;
					}
				}
				String message = String.format("Filter missed more than %s times.", 10000);
				throw new TooManyFilterMissesException(message);
			}

		};
	}

}
