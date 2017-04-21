package net.jqwik.properties;

import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.stream.*;

public class Arbitraries {

	public static <T> Arbitrary<T> fromGenerator(RandomGenerator<T> generator) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return generator;
			}
		};
	}

	@SafeVarargs
	public static <U> Arbitrary<U> of(U... values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	public static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(RandomGenerators.choose(enumClass));
	}

	private static <T> RandomGenerator<List<T>> createListGenerator(Arbitrary<T> elementArbitrary, int tries, int maxSize) {
		int elementTries = Math.max(maxSize / 2, 1) * tries;
		RandomGenerator<T> elementGenerator = elementArbitrary.generator(elementTries);
		return RandomGenerators.list(elementGenerator, maxSize);
	}

	public static Arbitrary<String> string(char[] validChars, int maxSize) {
		return new StringArbitrary(validChars, maxSize);
	}

	public static Arbitrary<String> string(char[] validChars) {
		return new StringArbitrary(validChars);
	}

	public static Arbitrary<String> string(char from, char to, int maxSize) {
		return new StringArbitrary(from, to, maxSize);
	}

	public static Arbitrary<String> string(char from, char to) {
		return new StringArbitrary(from, to);
	}

	public static Arbitrary<Integer> integer() {
		return new IntegerArbitrary();
	}

	public static Arbitrary<Integer> integer(int min, int max) {
		return new IntegerArbitrary(min, max);
	}

	public static Arbitrary<Long> longInteger(long min, long max) {
		return new LongArbitrary();
	}

	public static Arbitrary<Long> longInteger() {
		return new LongArbitrary();
	}


	public static <T> Arbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary, int maxSize) {
		return new Arbitrary<List<T>>() {
			@Override
			public RandomGenerator<List<T>> generator(int tries) {
				return createListGenerator(elementArbitrary, tries, maxSize);
			}
		};
	}

	public static <T> Arbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary) {
		return new Arbitrary<List<T>>() {
			@Override
			public RandomGenerator<List<T>> generator(int tries) {
				int maxSize = Arbitrary.defaultMaxFromTries(tries);
				return createListGenerator(elementArbitrary, tries, maxSize);
			}
		};
	}

	public static <T> Arbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary, int maxSize) {
		return listOf(elementArbitrary, maxSize).map(HashSet::new);
	}

	public static <T> Arbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary) {
		return listOf(elementArbitrary).map(HashSet::new);
	}

	public static <T> Arbitrary<Optional<T>> optionalOf(Arbitrary<T> elementArbitrary) {
		return elementArbitrary.injectNull(0.1).map(Optional::ofNullable);
	}

	public static <T> Arbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary, int maxSize) {
		return listOf(elementArbitrary, maxSize).map(Collection::stream);
	}

	public static <T> Arbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary) {
		return listOf(elementArbitrary).map(Collection::stream);
	}

	@SafeVarargs
	public static <T> Arbitrary<T> samples(T... samples) {
		return fromGenerator(RandomGenerators.samples(samples));
	}
}