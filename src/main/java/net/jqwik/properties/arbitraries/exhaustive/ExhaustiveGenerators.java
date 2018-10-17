package net.jqwik.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.support.*;

public class ExhaustiveGenerators {
	public static <T> Optional<ExhaustiveGenerator<T>> choose(List<T> values) {
		return fromIterable(values, values.size());
	}

	public static Optional<ExhaustiveGenerator<Character>> choose(char[] characters) {
		List<Character> validCharacters = new ArrayList<>(characters.length);
		for (char character : characters) {
			validCharacters.add(character);
		}
		return choose(validCharacters);
	}

	public static <T extends Enum<T>>  Optional<ExhaustiveGenerator<T>> choose(Class<T> enumClass) {
		return choose(Arrays.asList(enumClass.getEnumConstants()));
	}

	public static <T> Optional<ExhaustiveGenerator<T>> fromIterable(Iterable<T> iterator, long maxCount) {
		return Optional.of(new IterableBasedGenerator<>(iterator, maxCount));
	}

	public static <T> Optional<ExhaustiveGenerator<List<T>>> list(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		Optional<ExhaustiveGenerator<T>> exhaustiveElement = elementArbitrary.exhaustive();
		if (!exhaustiveElement.isPresent())
			return Optional.empty();
		long maxCount = calculateMaxCountForList(exhaustiveElement.get().maxCount(), minSize, maxSize);
		if (maxCount > Integer.MAX_VALUE)
			return Optional.empty();

		ExhaustiveGenerator<List<T>> generator = new ExhaustiveGenerator<List<T>>() {
			@Override
			public Iterator<List<T>> iterator() {
				return Combinatorics.listCombinations(exhaustiveElement.get(), minSize, maxSize);
			}

			@Override
			public long maxCount() {
				return maxCount;
			}
		};

		return Optional.of(generator);
	}

	private static long calculateMaxCountForList(long elementMaxCount, int minSize, int maxSize) {
		long sum = 0;
		for (int n = minSize; n <= maxSize; n++) {
			double choices = Math.pow(elementMaxCount, n);
			if (choices > Integer.MAX_VALUE) { // Stop when break off point reached
				return Long.MAX_VALUE;
			}
			sum += (long) choices;
		}
		return sum;
	}


	private static class IterableBasedGenerator<T> implements ExhaustiveGenerator<T> {

		final private Iterable<T> iterable;
		final private long maxCount;

		private IterableBasedGenerator(Iterable<T> iterable, long maxCount) {
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
}
