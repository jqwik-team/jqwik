package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class ExhaustiveGenerators {

	public static long MAXIMUM_ACCEPTED_MAX_COUNT = Integer.MAX_VALUE;

	public static <T> Optional<ExhaustiveGenerator<T>> create(Supplier<T> supplier) {
		return fromIterable(() -> new SupplierIterator<>(supplier), 1);
	}

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

	public static <T extends Enum<T>> Optional<ExhaustiveGenerator<T>> choose(Class<T> enumClass) {
		return choose(Arrays.asList(enumClass.getEnumConstants()));
	}

	public static <T> Optional<ExhaustiveGenerator<T>> fromIterable(Iterable<T> iterator, long maxCount) {
		return Optional.of(new IterableBasedExhaustiveGenerator<>(iterator, maxCount));
	}

	public static <T> Optional<ExhaustiveGenerator<List<T>>> list(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		Optional<Long> optionalMaxCount = ListExhaustiveGenerator.calculateMaxCount(elementArbitrary, minSize, maxSize);
		return optionalMaxCount.map(
			maxCount ->
			{
				ListExhaustiveGenerator<T> exhaustiveGenerator = new ListExhaustiveGenerator<>(elementArbitrary, maxCount, minSize, maxSize);

				// A hack to accommodate missing design idea for handling unique exhaustive generation:
				Optional<ExhaustiveGenerator<T>> exhaustive = elementArbitrary.exhaustive();
				if (exhaustive.isPresent() && exhaustive.get().isUnique()) {
					Predicate<List<T>> allElementsUnique = list -> list.size() == new HashSet<>(list).size();
					return exhaustiveGenerator.filter(allElementsUnique);
				} else {
					return exhaustiveGenerator;
				}
			}
		);
	}

	public static Optional<ExhaustiveGenerator<String>> strings(
		Arbitrary<Character> characterArbitrary,
		int minLength,
		int maxLength
	) {
		return list(characterArbitrary, minLength, maxLength).map(
			listGenerator -> listGenerator.map(
				listOfChars -> listOfChars.stream()
										  .map(String::valueOf)
										  .collect(Collectors.joining())
			));
	}

	public static <T> Optional<ExhaustiveGenerator<Set<T>>> set(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		Optional<Long> optionalMaxCount = SetExhaustiveGenerator.calculateMaxCount(elementArbitrary, minSize, maxSize);
		return optionalMaxCount.map(
			maxCount -> new SetExhaustiveGenerator<>(elementArbitrary, maxCount, minSize, maxSize)
		);
	}

	public static <R> Optional<ExhaustiveGenerator<R>> combine(List<Arbitrary<Object>> arbitraries, Function<List<Object>, R> combinator) {
		Optional<Long> optionalMaxCount = CombinedExhaustiveGenerator.calculateMaxCount(arbitraries);
		return optionalMaxCount.map(maxCount -> new CombinedExhaustiveGenerator<>(maxCount, arbitraries, combinator));
	}

	public static <T> Optional<ExhaustiveGenerator<List<T>>> shuffle(List<T> values) {
		Optional<Long> optionalMaxCount = PermutationExhaustiveGenerator.calculateMaxCount(values);
		return optionalMaxCount.map(
			maxCount -> new PermutationExhaustiveGenerator<>(values, maxCount)
		);
	}

	public static <U, T> Optional<ExhaustiveGenerator<U>> flatMap(ExhaustiveGenerator<T> base, Function<T, Arbitrary<U>> mapper) {
		Optional<Long> optionalMaxCount = FlatMappedExhaustiveGenerator.calculateMaxCounts(base, mapper);
		return optionalMaxCount.map(
			maxCount -> new FlatMappedExhaustiveGenerator<>(base, maxCount, mapper)
		);
	}

	private static class SupplierIterator<T> implements Iterator<T> {

		private final Supplier<T> supplier;
		private volatile boolean generated = false;

		private SupplierIterator(Supplier<T> supplier) {
			this.supplier = supplier;
		}

		@Override
		public boolean hasNext() {
			return !generated;
		}

		@Override
		public T next() {
			if (generated) {
				throw new NoSuchElementException();
			}
			generated = true;
			return supplier.get();
		}
	}
}
