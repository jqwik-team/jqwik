package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;

public class ExhaustiveGenerators {
	public static <T> Optional<ExhaustiveGenerator<T>> choose(List<T> values) {
		return fromIterator(values.iterator(), values.size());
	}

	public static <T extends Enum<T>>  Optional<ExhaustiveGenerator<T>> choose(Class<T> enumClass) {
		return choose(Arrays.asList(enumClass.getEnumConstants()));
	}

	public static <T> Optional<ExhaustiveGenerator<T>> fromIterator(Iterator<T> iterator, long maxCount) {
		return Optional.of(new IteratorBasedGenerator<>(iterator, maxCount));
	}

	private static class IteratorBasedGenerator<T> implements ExhaustiveGenerator<T> {

		final private Iterator<T> iterator ;
		final private long maxCount;

		private IteratorBasedGenerator(Iterator<T> iterator, long maxCount) {
			this.iterator = iterator;
			this.maxCount = maxCount;
		}

		@Override
		public long maxCount() {
			return maxCount;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public T next() {
			return iterator.next();
		}
	}
}
