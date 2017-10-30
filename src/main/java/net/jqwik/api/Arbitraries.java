package net.jqwik.api;

import java.math.*;
import java.util.*;
import java.util.stream.Stream;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

public class Arbitraries {

	public static <T> Arbitrary<T> fromGenerator(RandomGenerator<T> generator) {
		return tries -> generator;
	}

	@SafeVarargs
	public static <U> Arbitrary<U> of(U... values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	public static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(RandomGenerators.choose(enumClass));
	}

	public static Arbitrary<Integer> integer() {
		return new IntegerArbitrary();
	}

	public static Arbitrary<Integer> integer(int min, int max) {
		return new IntegerArbitrary(min, max);
	}

	public static Arbitrary<Long> longInteger(long min, long max) {
		return new LongArbitrary(min, max);
	}

	public static Arbitrary<Long> longInteger() {
		return new LongArbitrary();
	}

	public static Arbitrary<BigInteger> bigInteger(long min, long max) {
		return new LongArbitrary(min, max).map(aLong -> BigInteger.valueOf(aLong));
	}

	public static Arbitrary<BigInteger> bigInteger() {
		return new LongArbitrary().map(aLong -> BigInteger.valueOf(aLong));
	}

	public static Arbitrary<Float> floats() {
		return new FloatArbitrary();
	}

	public static Arbitrary<Float> floats(Float min, Float max, int scale) {
		return new FloatArbitrary(min, max, scale);
	}

	public static Arbitrary<BigDecimal> bigDecimal(double min, double max, int scale) {
		return new DoubleArbitrary(min, max, scale).map(aDouble -> BigDecimal.valueOf(aDouble));
	}

	public static Arbitrary<BigDecimal> bigDecimal() {
		return new DoubleArbitrary().map(aDouble -> BigDecimal.valueOf(aDouble));
	}

	public static Arbitrary<Double> doubles() {
		return new DoubleArbitrary();
	}

	public static Arbitrary<Double> doubles(double min, double max, int scale) {
		return new DoubleArbitrary(min, max, scale);
	}

	public static Arbitrary<String> string() {
		return new StringArbitrary();
	}

	public static Arbitrary<String> string(char[] validChars, int minLength, int maxLength) {
		return new StringArbitrary(validChars, minLength, maxLength);
	}

	public static Arbitrary<String> string(char[] validChars) {
		return new StringArbitrary(validChars);
	}

	public static Arbitrary<String> string(char from, char to, int minLength, int maxLength) {
		return new StringArbitrary(from, to, minLength, maxLength);
	}

	public static Arbitrary<String> string(char from, char to) {
		return new StringArbitrary(from, to);
	}

	public static <T> Arbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return new ListArbitrary<T>(elementArbitrary, minSize, maxSize);
	}

	public static <T> Arbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary) {
		return new ListArbitrary<T>(elementArbitrary);
	}

	public static <T> Arbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return new SetArbitrary<>(elementArbitrary, minSize, maxSize);
	}

	public static <T> Arbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary) {
		return new SetArbitrary<>(elementArbitrary);
	}

	public static <T> Arbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return new StreamArbitrary<>(elementArbitrary, minSize, maxSize);
	}

	public static <T> Arbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary) {
		return new StreamArbitrary<>(elementArbitrary);
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
		List<Shrinkable<T>> shrinkables = ShrinkableSample.of(samples);
		return fromGenerator(RandomGenerators.samples(shrinkables));
	}
}
