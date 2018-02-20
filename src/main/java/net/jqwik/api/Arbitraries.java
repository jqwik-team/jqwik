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

	public static IntegerArbitrary integers() {
		return new DefaultIntegerArbitrary();
	}

	@Deprecated
	public static Arbitrary<Integer> integers(int min, int max) {
		return integers().withMin(min).withMax(max);
	}

	public static LongArbitrary longs() {
		return new DefaultLongArbitrary();
	}

	@Deprecated
	public static LongArbitrary longs(long min, long max) {
		return longs().withMin(min).withMax(max);
	}

	public static BigIntegerArbitrary bigIntegers() {
		return new DefaultBigIntegerArbitrary();
	}

	@Deprecated
	public static BigIntegerArbitrary bigIntegers(long min, long max) {
		return bigIntegers().withMin(BigInteger.valueOf(min)).withMax(BigInteger.valueOf(max));
	}

	public static FloatArbitrary floats() {
		return new DefaultFloatArbitrary();
	}

	@Deprecated
	public static FloatArbitrary floats(Float min, Float max, int scale) {
		return floats().withMin(min).withMax(max).withScale(scale);
	}

	public static BigDecimalArbitrary bigDecimals() {
		return new DefaultBigDecimalArbitrary();
	}

	@Deprecated
	public static BigDecimalArbitrary bigDecimals(BigDecimal min, BigDecimal max, int scale) {
		return bigDecimals().withMin(min).withMax(max).withScale(scale);
	}

	public static DoubleArbitrary doubles() {
		return new DefaultDoubleArbitrary();
	}

	@Deprecated
	public static DoubleArbitrary doubles(double min, double max, int scale) {
		return doubles().withMin(min).withMax(max).withScale(scale);
	}

	public static ByteArbitrary bytes() {
		return new DefaultByteArbitrary();
	}

	@Deprecated
	public static ByteArbitrary bytes(byte min, byte max) {
		return bytes().withMin(min).withMax(max);
	}

	public static ShortArbitrary shorts() {
		return new DefaultShortArbitrary();
	}

	@Deprecated
	public static ShortArbitrary shorts(short min, short max) {
		return shorts().withMin(min).withMax(max);
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
