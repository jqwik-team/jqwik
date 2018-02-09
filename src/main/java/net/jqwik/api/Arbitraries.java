package net.jqwik.api;

import net.jqwik.api.arbitraries.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Arbitraries {

	private Arbitraries() {
	}

	public static <T> Arbitrary<T> fromGenerator(RandomGenerator<T> generator) {
		return tries -> generator;
	}

	public static <T> Arbitrary<T> randomValue(Function<Random, T> generator) {
		return fromGenerator(random -> Shrinkable.unshrinkable(generator.apply(random)));
	}

	public static Arbitrary<Random> randoms() {
		return randomValue(random -> new Random(random.nextLong()));
	}

	@SafeVarargs
	public static <U> Arbitrary<U> of(U... values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	public static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(RandomGenerators.choose(enumClass));
	}

	public static Arbitrary<Integer> integers() {
		return new IntegerArbitrary();
	}

	public static Arbitrary<Integer> integers(int min, int max) {
		return new IntegerArbitrary(min, max);
	}

	public static Arbitrary<Long> longs(long min, long max) {
		return new LongArbitrary(min, max);
	}

	public static Arbitrary<Long> longs() {
		return new LongArbitrary();
	}

	public static Arbitrary<BigInteger> bigIntegers(long min, long max) {
		return new LongArbitrary(min, max).map(aLong -> BigInteger.valueOf(aLong));
	}

	public static Arbitrary<BigInteger> bigIntegers() {
		return new LongArbitrary().map(aLong -> BigInteger.valueOf(aLong));
	}

	public static Arbitrary<Float> floats() {
		return new FloatArbitrary();
	}

	public static Arbitrary<Float> floats(Float min, Float max, int scale) {
		return new FloatArbitrary(min, max, scale);
	}

	public static Arbitrary<BigDecimal> bigDecimals(BigDecimal min, BigDecimal max, int scale) {
		return new BigDecimalArbitrary(min, max, scale);
	}

	public static Arbitrary<BigDecimal> bigDecimals() {
		return new BigDecimalArbitrary();
	}

	public static Arbitrary<Double> doubles() {
		return new DoubleArbitrary();
	}

	public static Arbitrary<Double> doubles(double min, double max, int scale) {
		return new DoubleArbitrary(min, max, scale);
	}

	public static Arbitrary<Byte> bytes() {
		return new ByteArbitrary();
	}

	public static Arbitrary<Byte> bytes(byte min, byte max) {
		return new ByteArbitrary(min, max);
	}

	public static Arbitrary<Short> shorts() {
		return new ShortArbitrary();
	}

	public static Arbitrary<Short> shorts(short min, short max) {
		return new ShortArbitrary(min, max);
	}

	public static Arbitrary<String> strings() {
		return new StringArbitrary();
	}

	public static Arbitrary<String> strings(char[] validChars, int minLength, int maxLength) {
		return new StringArbitrary(validChars, minLength, maxLength);
	}

	public static Arbitrary<String> strings(char[] validChars) {
		return new StringArbitrary(validChars);
	}

	public static Arbitrary<String> strings(char from, char to, int minLength, int maxLength) {
		return new StringArbitrary(from, to, minLength, maxLength);
	}

	public static Arbitrary<String> strings(char from, char to) {
		return new StringArbitrary(from, to);
	}

	public static <T> Arbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return new ListArbitrary<T>(elementArbitrary, minSize, maxSize);
	}

	public static CharacterArbitrary chars() {
		return new DefaultCharacterArbitrary();
	}

	public static CharacterArbitrary chars(char from, char to) {
		return chars().withChars(from, to);
	}

	public static CharacterArbitrary chars(char[] validChars) {
		return chars().withChars(validChars);
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

	public static <A, T> Arbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return new ArrayArbitrary(arrayClass, elementArbitrary, minSize, maxSize);
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
