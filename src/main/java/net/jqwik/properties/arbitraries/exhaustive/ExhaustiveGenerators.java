package net.jqwik.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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

	public static <T> Optional<ExhaustiveGenerator<Set<T>>> set(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		Optional<ExhaustiveGenerator<T>> exhaustiveElement = elementArbitrary.exhaustive();
		if (!exhaustiveElement.isPresent())
			return Optional.empty();
		long maxCount = calculateMaxCountForSet(exhaustiveElement.get().maxCount(), minSize, maxSize);
		if (maxCount > Integer.MAX_VALUE)
			return Optional.empty();

		ExhaustiveGenerator<Set<T>> generator = new ExhaustiveGenerator<Set<T>>() {
			@Override
			public Iterator<Set<T>> iterator() {
				return Combinatorics.setCombinations(exhaustiveElement.get(), minSize, maxSize);
			}

			@Override
			public long maxCount() {
				return maxCount;
			}
		};

		return Optional.of(generator);
	}

	private static long calculateMaxCountForSet(long elementMaxCount, int minSize, int maxSize) {
		long sum = 0;
		for (int n = minSize; n <= maxSize; n++) {
			if (n == 0) { // empty set
				sum += 1;
				continue;
			}
			if (elementMaxCount < n) { // empty set
				continue;
			}
			long choices = factorial(elementMaxCount) / (factorial(elementMaxCount - n) * factorial(n));
			if (choices > Integer.MAX_VALUE || choices < 0) { // Stop when break off point reached
				return Long.MAX_VALUE;
			}
			sum += choices;
		}
		return sum;
	}

	private static long factorial(long number) {
		long result = 1;

		for (long factor = 2; factor <= number; factor++) {
			result *= factor;
		}

		return result;
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

	public static <R> Optional<ExhaustiveGenerator<R>> combine(List<Arbitrary<Object>> arbitraries, Function<List<Object>, R> combinator) {
		Optional<Long> optionalMaxCount = calculateMaxCountForCombination(arbitraries);
		return optionalMaxCount.map(maxCount -> new ExhaustiveGenerator<R>() {
			@Override
			public long maxCount() {
				return maxCount;
			}

			@Override
			public Iterator<R> iterator() {
				List<Iterable<Object>> iterables = arbitraries
					.stream()
					.map(a -> (Iterable<Object>) a.exhaustive().get())
					.collect(Collectors.toList());
				Iterator<List<Object>> valuesIterator = Combinatorics.combine(iterables);

				return new Iterator<R>() {
					@Override
					public boolean hasNext() {
						return valuesIterator.hasNext();
					}

					@Override
					public R next() {
						List<Object> values = valuesIterator.next();
						return combinator.apply(values);
					}
				};
			}
		});
	}

	private static Optional<Long> calculateMaxCountForCombination(List<Arbitrary<Object>> arbitraries) {
		long product = 1;
		for (Arbitrary<Object> arbitrary : arbitraries) {
			Optional<ExhaustiveGenerator<Object>> exhaustive = arbitrary.exhaustive();
			if (!exhaustive.isPresent()) {
				return Optional.empty();
			}
			product *= exhaustive.get().maxCount();
			if (product > Integer.MAX_VALUE) {
				return Optional.empty();
			}
		}
		return Optional.of(product);
	}

}
