package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.properties.arbitraries.*;

public class NArbitraries {

	public static <T> NArbitrary<T> fromGenerator(NShrinkableGenerator<T> generator) {
		return tries -> generator;
	}

	@SafeVarargs
	public static <U> NArbitrary<U> of(U... values) {
		return fromGenerator(NShrinkableGenerators.choose(values));
	}

	public static <T extends Enum> NArbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(NShrinkableGenerators.choose(enumClass));
	}

	public static NArbitrary<Integer> integer() {
		return new NIntegerArbitrary();
	}

	public static NArbitrary<Integer> integer(int min, int max) {
		return new NIntegerArbitrary(min, max);
	}

	public static NArbitrary<Long> longInteger(long min, long max) {
		return new NLongArbitrary();
	}

	public static NArbitrary<Long> longInteger() {
		return new NLongArbitrary();
	}

	public static NArbitrary<String> string() {
		return new NStringArbitrary();
	}

	public static NArbitrary<String> string(char[] validChars, int maxSize) {
		return new NStringArbitrary(validChars, maxSize);
	}

	public static NArbitrary<String> string(char[] validChars) {
		return new NStringArbitrary(validChars);
	}

	public static NArbitrary<String> string(char from, char to, int maxSize) {
		return new NStringArbitrary(from, to, maxSize);
	}

	public static NArbitrary<String> string(char from, char to) {
		return new NStringArbitrary(from, to);
	}

	public static <T> NArbitrary<List<T>> listOf(NArbitrary<T> elementArbitrary, int maxSize) {
		return new NListArbitrary<T>(elementArbitrary, maxSize);
	}

	public static <T> NArbitrary<List<T>> listOf(NArbitrary<T> elementArbitrary) {
		return new NListArbitrary<T>(elementArbitrary);
	}

	public static <T> NArbitrary<Set<T>> setOf(NArbitrary<T> elementArbitrary, int maxSize) {
		return new NSetArbitrary<>(elementArbitrary, maxSize);
	}

	public static <T> NArbitrary<Set<T>> setOf(NArbitrary<T> elementArbitrary) {
		return new NSetArbitrary<>(elementArbitrary);
	}

	public static <T> NArbitrary<Stream<T>> streamOf(NArbitrary<T> elementArbitrary, int maxSize) {
		return new NStreamArbitrary<>(elementArbitrary, maxSize);
	}

	public static <T> NArbitrary<Stream<T>> streamOf(NArbitrary<T> elementArbitrary) {
		return new NStreamArbitrary<>(elementArbitrary);
	}

	public static <T> NArbitrary<Optional<T>> optionalOf(NArbitrary<T> elementArbitrary) {
		return elementArbitrary.injectNull(0.1).map(Optional::ofNullable);
	}

	public static <A, T> NArbitrary<A> arrayOf(Class<A> arrayClass, NArbitrary<T> elementArbitrary, int maxSize) {
		return new NArrayArbitrary(arrayClass, elementArbitrary, maxSize);
	}

	public static <A, T> NArbitrary<A> arrayOf(Class<A> arrayClass, NArbitrary<T> elementArbitrary) {
		return new NArrayArbitrary(arrayClass, elementArbitrary);
	}


	@SafeVarargs
	public static <T> NArbitrary<T> samples(T... samples) {
		return fromGenerator(NShrinkableGenerators.samples(samples));
	}

}
