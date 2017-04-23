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

	public static Arbitrary<?> string() {
		return new StringArbitrary();
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
		return new ListArbitrary<T>(elementArbitrary, maxSize);
	}

	public static <T> Arbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary) {
		return new ListArbitrary<T>(elementArbitrary);
	}

	public static <T> Arbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary, int maxSize) {
		return new SetArbitrary(elementArbitrary, maxSize);
	}

	public static <T> Arbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary) {
		return new SetArbitrary(elementArbitrary);
	}

	public static <T> Arbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary, int maxSize) {
		return new StreamArbitrary(elementArbitrary, maxSize);
	}

	public static <T> Arbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary) {
		return new StreamArbitrary(elementArbitrary);
	}

	public static <T> Arbitrary<Optional<T>> optionalOf(Arbitrary<T> elementArbitrary) {
		return elementArbitrary.injectNull(0.1).map(Optional::ofNullable);
	}

	public static <A, T> Arbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary, int maxSize) {
		return new ArrayArbitrary(arrayClass, elementArbitrary, maxSize);
	}

	public static <A, T> Arbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary) {
		return new ArrayArbitrary(arrayClass, elementArbitrary);
	}

	@SafeVarargs
	public static <T> Arbitrary<T> samples(T... samples) {
		return fromGenerator(RandomGenerators.samples(samples));
	}

}