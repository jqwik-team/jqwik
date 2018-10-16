package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;

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
