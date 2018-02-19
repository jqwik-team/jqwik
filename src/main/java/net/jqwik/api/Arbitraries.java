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

	public static FloatArbitrary floats() {
		return new DefaultFloatArbitrary();
	}

	@Deprecated
	public static FloatArbitrary floats(Float min, Float max, int scale) {
		return floats().withMin(min).withMax(max).withScale(scale);
	}

	public static Arbitrary<BigDecimal> bigDecimals(BigDecimal min, BigDecimal max, int scale) {
		return new BigDecimalArbitrary(min, max, scale);
	}

	public static Arbitrary<BigDecimal> bigDecimals() {
		return new BigDecimalArbitrary();
	}

	public static DoubleArbitrary doubles() {
		return new DefaultDoubleArbitrary();
	}

	@Deprecated
	public static DoubleArbitrary doubles(double min, double max, int scale) {
		return doubles().withMin(min).withMax(max).withScale(scale);
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

	public static StringArbitrary strings() {
		return new DefaultStringArbitrary();
	}

	@Deprecated
	public static StringArbitrary strings(char[] validChars, int minLength, int maxLength) {
		return strings().withChars(validChars).withMinLength(minLength).withMaxLength(maxLength);
	}

	@Deprecated
	public static StringArbitrary strings(char[] validChars) {
		return strings().withChars(validChars);
	}

	@Deprecated
	public static StringArbitrary strings(char from, char to, int minLength, int maxLength) {
		return strings().withChars(from, to).withMinLength(minLength).withMaxLength(maxLength);
	}

	@Deprecated
	public static StringArbitrary strings(char from, char to) {
		return strings().withChars(from, to);
	}

	public static CharacterArbitrary chars() {
		return new DefaultCharacterArbitrary();
	}

	@Deprecated
	public static CharacterArbitrary chars(char from, char to) {
		return chars().withChars(from, to);
	}

	@Deprecated
	public static CharacterArbitrary chars(char[] validChars) {
		return chars().withChars(validChars);
	}

	public static <T> SizableArbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary) {
		return new ListArbitrary<T>(elementArbitrary);
	}

	@Deprecated
	public static <T> SizableArbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return listOf(elementArbitrary).withMinSize(minSize).withMaxSize(maxSize);
	}

	public static <T> SizableArbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary) {
		return new SetArbitrary<>(elementArbitrary);
	}

	@Deprecated
	public static <T> SizableArbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return setOf(elementArbitrary).withMinSize(minSize).withMaxSize(maxSize);
	}

	public static <T> SizableArbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary) {
		return new StreamArbitrary<>(elementArbitrary);
	}

	@Deprecated
	public static <T> SizableArbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return streamOf(elementArbitrary).withMinSize(minSize).withMaxSize(maxSize);
	}

	public static <T> Arbitrary<Optional<T>> optionalOf(Arbitrary<T> elementArbitrary) {
		return elementArbitrary.injectNull(0.1).map(Optional::ofNullable);
	}

	public static <A, T> SizableArbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary) {
		return new ArrayArbitrary<>(arrayClass, elementArbitrary);
	}

	@Deprecated
	public static <A, T> SizableArbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary, int minSize, int maxSize) {
		return arrayOf(arrayClass, elementArbitrary).withMinSize(minSize).withMaxSize(maxSize);
	}

	@SafeVarargs
	public static <T> Arbitrary<T> samples(T... samples) {
		List<Shrinkable<T>> shrinkables = ShrinkableSample.of(samples);
		return fromGenerator(RandomGenerators.samples(shrinkables));
	}

}
