package net.jqwik.api;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.properties.stateful.*;
import net.jqwik.providers.*;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Arbitraries {

	private Arbitraries() {
	}

	/**
	 * Create an arbitrary of type T from a corresponding generator of type T.
	 *
	 * @param generator The generator to be used for generating the values
	 * @param <T>       The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> fromGenerator(RandomGenerator<T> generator) {
		return tries -> generator;
	}

	/**
	 * Create an arbitrary that will generate values of type T using a generator function.
	 * The generated values are unshrinkable.
	 *
	 * @param generator The generator function to be used for generating the values
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> randomValue(Function<Random, T> generator) {
		return fromGenerator(random -> Shrinkable.unshrinkable(generator.apply(random)));
	}

	/**
	 * Create an arbitrary for Random objects.
	 *
	 * @return a new arbitrary instance
	 */
	public static Arbitrary<Random> randoms() {
		return randomValue(random -> new Random(random.nextLong()));
	}

	/**
	 * Create an arbitrary that will randomly choose from a given array of values.
	 * A generated value will be shrunk towards the start of the array.
	 *
	 * @param values The array of values to choose from
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SafeVarargs
	public static <T> Arbitrary<T> of(T... values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	/**
	 * Create an arbitrary that will randomly choose from a given list of values.
	 * A generated value will be shrunk towards the start of the list.
	 *
	 * @param values The list of values to choose from
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> of(List<T> values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	/**
	 * Create an arbitrary of character values.
	 *
	 * @param values The array of characters to choose from.
	 * @return a new arbitrary instance
	 */
	public static Arbitrary<Character> of(char[] values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	/**
	 * Create an arbitrary for enum values of type T.
	 *
	 * @param enumClass The enum class.
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(RandomGenerators.choose(enumClass));
	}

	/**
	 * Create an arbitrary that will randomly choose between all given arbitraries of the same type T.
	 *
	 * @param first The first arbitrary to choose form
	 * @param rest An array of arbitraries to choose from
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SafeVarargs
	public static <T> Arbitrary<T> oneOf(Arbitrary<T> first, Arbitrary<T>... rest) {
		if (rest.length == 0) {
			return first;
		}
		List<Arbitrary<T>> all = new ArrayList<>(Arrays.asList(rest));
		all.add(first);
		return oneOf(all);
	}

	/**
	 * Create an arbitrary that will randomly choose between all given arbitraries of the same type T.
	 *
	 * @param all A list of arbitraries to choose from
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> oneOf(List<Arbitrary<T>> all) {
		return of(all).flatMap(Function.identity());
	}

	/**
	 * Create an arbitrary that will randomly choose between all given values of the same type T.
	 * The probability distribution is weighted with the first parameter of the tuple.
	 *
	 * @param frequencies An array of tuples of which the first parameter gives the weight and the second the value.
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SafeVarargs
	public static <T> Arbitrary<T> frequency(Tuples.Tuple2<Integer, T> ... frequencies) {
		return fromGenerator(RandomGenerators.frequency(frequencies));
	}

	/**
	 * Create an arbitrary that generates values of type Integer.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that generates values of type Long.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that generates values of type BigInteger.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that generates values of type Float.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that generates values of type BigDecimal.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that generates values of type Double.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that generates values of type Byte.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that generates values of type Short.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that generates values of type String.
	 *
	 * @return a new arbitrary instance
	 */
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

	/**
	 * Create an arbitrary that will provide the sample values from first to last
	 * and then start again at the beginning. Shrinking of samples is tried
	 * towards the start of the samples.
	 *
	 * @param samples The array of sample values
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SafeVarargs
	public static <T> Arbitrary<T> samples(T... samples) {
		return fromGenerator(RandomGenerators.samples(samples));
	}

	/**
	 * Create an arbitrary that will always generate the same value.
	 *
	 * @param value The value to "generate"
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> constant(T value) {
		return fromGenerator(random -> Shrinkable.unshrinkable(value));
	}

	/**
	 * Find a registered arbitrary that will be used to generate values of type T.
	 * All default arbitrary providers and all registered arbitrary providers are considered.
	 * This is more or less the same mechanism that jqwik uses to find arbitraries for
	 * property method parameters.
	 *
	 * @param type The type of the value to find an arbitrary for
	 * @param typeParameters The type parameters if type is a generic type
	 * @param <T> The type of values to generate
	 *
	 * @return a new arbitrary instance
	 * @throws CannotFindArbitraryException if there is no registered arbitrary provider to serve this type
	 */
	public static <T> Arbitrary<T> defaultFor(Class<T> type, Class<?>... typeParameters) {
		TypeUsage[] genericTypeParameters =
			Arrays.stream(typeParameters)
				  .map(TypeUsage::of)
				  .toArray(TypeUsage[]::new);
		return firstDefaultFor(TypeUsage.of(type, genericTypeParameters));
	}

	private static <T> Arbitrary<T> firstDefaultFor(TypeUsage typeUsage) {
		Set<Arbitrary<?>> arbitraries = allDefaultsFor(typeUsage);
		if (arbitraries.isEmpty()) {
			throw new CannotFindArbitraryException(typeUsage);
		}

		//TODO: Handle case if there is more than one fitting default provider
		return (Arbitrary<T>) arbitraries.iterator().next();
	}

	private static Set<Arbitrary<?>> allDefaultsFor(TypeUsage typeUsage) {
		RegisteredArbitraryResolver defaultArbitraryResolver =
			new RegisteredArbitraryResolver(RegisteredArbitraryProviders.getProviders());
		Function<TypeUsage, Set<Arbitrary<?>>> subtypeProvider = Arbitraries::allDefaultsFor;
		return defaultArbitraryResolver.resolve(typeUsage, subtypeProvider);
	}

	/**
	 * Create an arbitrary that will evaluate arbitrarySupplier as soon as it is used for generating values.
	 *
	 * This is useful (and necessary) when arbitrary providing functions use other arbitrary providing functions
	 * in a recursive way. Without the use of lazy() this would result in a stack overflow.
	 *
	 * @param arbitrarySupplier The supplier function being used to generate an arbitrary
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> lazy(Supplier<Arbitrary<T>> arbitrarySupplier) {
		return genSize -> arbitrarySupplier.get().generator(genSize);
	}

	/**
	 * Create an arbitrary to create a sequence of actions. Useful for stateful testing.
	 *
	 * @param actionArbitrary The arbitrary to generate individual actions.
	 * @param <M> The type of actions to generate
	 * @return a new arbitrary instance
	 */
	public static <M> ActionSequenceArbitrary<M> sequences(Arbitrary<Action<M>> actionArbitrary) {
		return SequentialActionSequence.fromActions(actionArbitrary);
	}
}
