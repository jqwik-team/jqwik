package net.jqwik.api;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.properties.arbitraries.*;

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

	public static Arbitrary<Character> of(char[] values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(RandomGenerators.choose(enumClass));
	}

	@SafeVarargs
	public static <T> Arbitrary<T> oneOf(Arbitrary<T> first, Arbitrary<T>... rest) {
		if (rest.length == 0) {
			return first;
		}
		List<Arbitrary<T>> all = new ArrayList<>(Arrays.asList(rest));
		all.add(first);
		return fromGenerator(RandomGenerators.choose(all)).flatMap(arbitrary -> arbitrary);
	}

	public static IntegerArbitrary integers() {
		return new DefaultIntegerArbitrary();
	}

	/**
	 * @deprecated use {@code integers().between(min, max)} instead.
	 */
	@Deprecated
	public static Arbitrary<Integer> integers(int min, int max) {
		return integers().between(min, max);
	}

	public static LongArbitrary longs() {
		return new DefaultLongArbitrary();
	}

	/**
	 * @deprecated use {@code longs().between(min, max)} instead.
	 */
	@Deprecated
	public static LongArbitrary longs(long min, long max) {
		return longs().between(min, max);
	}

	public static BigIntegerArbitrary bigIntegers() {
		return new DefaultBigIntegerArbitrary();
	}

	/**
	 * @deprecated use {@code bigIntegers().between(min, max)} instead.
	 */
	@Deprecated
	public static BigIntegerArbitrary bigIntegers(long min, long max) {
		return bigIntegers().between(BigInteger.valueOf(min), BigInteger.valueOf(max));
	}

	public static FloatArbitrary floats() {
		return new DefaultFloatArbitrary();
	}

	/**
	 * @deprecated use {@code floats().between(min, max)} instead.
	 */
	@Deprecated
	public static FloatArbitrary floats(Float min, Float max, int scale) {
		return floats().between(min, max).ofScale(scale);
	}

	public static BigDecimalArbitrary bigDecimals() {
		return new DefaultBigDecimalArbitrary();
	}

	/**
	 * @deprecated use {@code bigDecimals().between(min, max)} instead.
	 */
	@Deprecated
	public static BigDecimalArbitrary bigDecimals(BigDecimal min, BigDecimal max, int scale) {
		return bigDecimals().between(min, max).ofScale(scale);
	}

	public static DoubleArbitrary doubles() {
		return new DefaultDoubleArbitrary();
	}

	/**
	 * @deprecated use {@code doubles().between(min, max)} instead.
	 */
	@Deprecated
	public static DoubleArbitrary doubles(double min, double max, int scale) {
		return doubles().between(min, max).ofScale(scale);
	}

	public static ByteArbitrary bytes() {
		return new DefaultByteArbitrary();
	}

	/**
	 * @deprecated use {@code bytes().between(min, max)} instead.
	 */
	@Deprecated
	public static ByteArbitrary bytes(byte min, byte max) {
		return bytes().between(min, max);
	}

	public static ShortArbitrary shorts() {
		return new DefaultShortArbitrary();
	}

	/**
	 * @deprecated use {@code shorts().between(min, max)} instead.
	 */
	@Deprecated
	public static ShortArbitrary shorts(short min, short max) {
		return shorts().between(min, max);
	}

	public static StringArbitrary strings() {
		return new DefaultStringArbitrary();
	}

	/**
	 * @deprecated use {@code strings().withChars(validChars).ofMinLength(minLength).ofMaxLength(maxLength)} instead.
	 */
	@Deprecated
	public static StringArbitrary strings(char[] validChars, int minLength, int maxLength) {
		return strings().withChars(validChars).ofMinLength(minLength).ofMaxLength(maxLength);
	}

	/**
	 * @deprecated use {@code strings().withChars(validChars)} instead.
	 */
	@Deprecated
	public static StringArbitrary strings(char[] validChars) {
		return strings().withChars(validChars);
	}

	/**
	 * @deprecated use {@code strings().withCharRange(from, to).ofMinLength(minLength).ofMaxLength(maxLength)} instead.
	 */
	@Deprecated
	public static StringArbitrary strings(char from, char to, int minLength, int maxLength) {
		return strings().withCharRange(from, to).ofMinLength(minLength).ofMaxLength(maxLength);
	}

	/**
	 * @deprecated use {@code strings().withCharRange(from, to)} instead.
	 */
	@Deprecated
	public static StringArbitrary strings(char from, char to) {
		return strings().withCharRange(from, to);
	}

	public static CharacterArbitrary chars() {
		return new DefaultCharacterArbitrary().all();
	}

	/**
	 * @deprecated use {@code chars().between(from, to)} instead.
	 */
	@Deprecated
	public static CharacterArbitrary chars(char from, char to) {
		return chars().between(from, to);
	}

	/**
	 * @deprecated use {@code elementArbitrary.list()} instead.
	 */
	@Deprecated
	public static <T> SizableArbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary) {
		return elementArbitrary.list();
	}

	/**
	 * @deprecated use {@code elementArbitrary.list().ofMinSize(minSize).ofMaxSize(maxSize)} instead.
	 */
	@Deprecated
	public static <T> SizableArbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return elementArbitrary.list().ofMinSize(minSize).ofMaxSize(maxSize);
	}

	/**
	 * @deprecated use {@code elementArbitrary.set()} instead.
	 */
	@Deprecated
	public static <T> SizableArbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary) {
		return elementArbitrary.set();
	}

	/**
	 * @deprecated use {@code elementArbitrary.set().ofMinSize(minSize).ofMaxSize(maxSize)} instead.
	 */
	@Deprecated
	public static <T> SizableArbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return elementArbitrary.set().ofMinSize(minSize).ofMaxSize(maxSize);
	}

	/**
	 * @deprecated use {@code elementArbitrary.stream()} instead.
	 */
	@Deprecated
	public static <T> SizableArbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary) {
		return elementArbitrary.stream();
	}

	/**
	 * @deprecated use {@code elementArbitrary.stream().ofMinSize(minSize).ofMaxSize(maxSize)} instead.
	 */
	@Deprecated
	public static <T> SizableArbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return elementArbitrary.stream().ofMinSize(minSize).ofMaxSize(maxSize);
	}

	/**
	 * @deprecated use {@code elementArbitrary.optional()} instead.
	 */
	@Deprecated
	public static <T> Arbitrary<Optional<T>> optionalOf(Arbitrary<T> elementArbitrary) {
		return elementArbitrary.optional();
	}

	/**
	 * @deprecated use {@code elementArbitrary.array(arrayClass)} instead.
	 */
	@Deprecated
	public static <A, T> SizableArbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary) {
		return elementArbitrary.array(arrayClass);
	}

	/**
	 * @deprecated use {@code elementArbitrary.array(arrayClass).ofMinSize(minSize).ofMaxSize(maxSize)} instead.
	 */
	@Deprecated
	public static <A, T> SizableArbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return elementArbitrary.array(arrayClass).ofMinSize(minSize).ofMaxSize(maxSize);
	}

	@SafeVarargs
	public static <T> Arbitrary<T> samples(T... samples) {
		List<Shrinkable<T>> shrinkables = ShrinkableSample.of(samples);
		return fromGenerator(RandomGenerators.samples(shrinkables));
	}

	public static <T> Arbitrary<T> constant(T value) {
		return fromGenerator(random -> Shrinkable.unshrinkable(value));
	}
}
