package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.stateful.*;

public class Arbitraries {

	public static abstract class ArbitrariesFacade {
		private static ArbitrariesFacade implementation;

		static {
			implementation = FacadeLoader.load(ArbitrariesFacade.class);
		}

		public abstract <T> RandomGenerator<T> randomChoose(List<T> values);

		public abstract <T> Optional<ExhaustiveGenerator<T>> exhaustiveChoose(List<T> values);

		public abstract RandomGenerator<Character> randomChoose(char[] values);

		public abstract Optional<ExhaustiveGenerator<Character>> exhaustiveChoose(char[] values);

		public abstract <T extends Enum> RandomGenerator<T> randomChoose(Class<T> enumClass);

		public abstract <T extends Enum> Optional<ExhaustiveGenerator<T>> exhaustiveChoose(Class<T> enumClass);

		public abstract <T> Arbitrary<T> oneOf(List<Arbitrary<T>> all);

		public abstract <T> RandomGenerator<T> randomFrequency(List<Tuple.Tuple2<Integer, T>> frequencies);

		public abstract <T> RandomGenerator<T> randomSamples(T[] samples);

		public abstract <T> RandomGenerator<List<T>> randomShuffle(List<T> values);

		public abstract <T> Optional<ExhaustiveGenerator<List<T>>> exhaustiveShuffle(List<T> values);

		public abstract <M> ActionSequenceArbitrary<M> sequences(Arbitrary<Action<M>> actionArbitrary);

		public abstract <T> Arbitrary<T> frequencyOf(List<Tuple.Tuple2<Integer, Arbitrary<T>>> frequencies);

		public abstract IntegerArbitrary integers();

		public abstract LongArbitrary longs();

		public abstract BigIntegerArbitrary bigIntegers();

		public abstract FloatArbitrary floats();

		public abstract BigDecimalArbitrary bigDecimals();

		public abstract DoubleArbitrary doubles();

		public abstract ByteArbitrary bytes();

		public abstract ShortArbitrary shorts();

		public abstract StringArbitrary strings();

		public abstract CharacterArbitrary chars();

		public abstract <T> Arbitrary<T> defaultFor(Class<T> type, Class<?>[] typeParameters);
	}

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
		return of(Arrays.asList(values));
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
		return fromGenerators(
			ArbitrariesFacade.implementation.randomChoose(values),
			ArbitrariesFacade.implementation.exhaustiveChoose(values)
		);
	}

	/**
	 * Create an arbitrary of character values.
	 *
	 * @param values The array of characters to choose from.
	 * @return a new arbitrary instance
	 */
	public static Arbitrary<Character> of(char[] values) {
		return fromGenerators(
			ArbitrariesFacade.implementation.randomChoose(values),
			ArbitrariesFacade.implementation.exhaustiveChoose(values)
		);
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
		return fromGenerators(
			ArbitrariesFacade.implementation.randomChoose(enumClass),
			ArbitrariesFacade.implementation.exhaustiveChoose(enumClass)
		);
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
		if (all.size() == 1) {
			return all.get(0);
		}
		// Simple flatMapping is not enough because of configurations
		return ArbitrariesFacade.implementation.oneOf(all);
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
	public static <T> Arbitrary<T> frequency(Tuple.Tuple2<Integer, T> ... frequencies) {
		return frequency(Arrays.asList(frequencies));
	}

	/**
	 * Create an arbitrary that will randomly choose between all given values of the same type T.
	 * The probability distribution is weighted with the first parameter of the tuple.
	 *
	 * @param frequencies A list of tuples of which the first parameter gives the weight and the second the value.
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> frequency(List<Tuple.Tuple2<Integer, T>> frequencies) {
		List<T> values = frequencies.stream()
			.filter(f -> f.get1() > 0)
			.map(Tuple.Tuple2::get2)
			.collect(Collectors.toList());

		return fromGenerators(
			ArbitrariesFacade.implementation.randomFrequency(frequencies),
			ArbitrariesFacade.implementation.exhaustiveChoose(values)
		);

	}

	/**
	 * Create an arbitrary that will randomly choose between all given arbitraries of the same type T.
	 * The probability distribution is weighted with the first parameter of the tuple.
	 *
	 * @param frequencies An array of tuples of which the first parameter gives the weight and the second the arbitrary.
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SafeVarargs
	public static <T> Arbitrary<T> frequencyOf(Tuple.Tuple2<Integer, Arbitrary<T>> ... frequencies) {
		return frequencyOf(Arrays.asList(frequencies));
	}

	/**
	 * Create an arbitrary that will randomly choose between all given arbitraries of the same type T.
	 * The probability distribution is weighted with the first parameter of the tuple.
	 *
	 * @param frequencies A list of tuples of which the first parameter gives the weight and the second the arbitrary.
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> frequencyOf(List<Tuple.Tuple2<Integer, Arbitrary<T>>> frequencies) {
		// Simple flatMapping is not enough because of configurations
		return ArbitrariesFacade.implementation.frequencyOf(frequencies);
	}

	/**
	 * Create an arbitrary that generates values of type Integer.
	 *
	 * @return a new arbitrary instance
	 */
	public static IntegerArbitrary integers() {
		return ArbitrariesFacade.implementation.integers();
	}

	/**
	 * Create an arbitrary that generates values of type Long.
	 *
	 * @return a new arbitrary instance
	 */
	public static LongArbitrary longs() {
		return ArbitrariesFacade.implementation.longs();
	}

	/**
	 * Create an arbitrary that generates values of type BigInteger.
	 *
	 * @return a new arbitrary instance
	 */
	public static BigIntegerArbitrary bigIntegers() {
		return ArbitrariesFacade.implementation.bigIntegers();
	}

	/**
	 * Create an arbitrary that generates values of type Float.
	 *
	 * @return a new arbitrary instance
	 */
	public static FloatArbitrary floats() {
		return ArbitrariesFacade.implementation.floats();
	}

	/**
	 * Create an arbitrary that generates values of type BigDecimal.
	 *
	 * @return a new arbitrary instance
	 */
	public static BigDecimalArbitrary bigDecimals() {
		return ArbitrariesFacade.implementation.bigDecimals();
	}

	/**
	 * Create an arbitrary that generates values of type Double.
	 *
	 * @return a new arbitrary instance
	 */
	public static DoubleArbitrary doubles() {
		return ArbitrariesFacade.implementation.doubles();
	}

	/**
	 * Create an arbitrary that generates values of type Byte.
	 *
	 * @return a new arbitrary instance
	 */
	public static ByteArbitrary bytes() {
		return ArbitrariesFacade.implementation.bytes();
	}

	/**
	 * Create an arbitrary that generates values of type Short.
	 *
	 * @return a new arbitrary instance
	 */
	public static ShortArbitrary shorts() {
		return ArbitrariesFacade.implementation.shorts();
	}

	/**
	 * Create an arbitrary that generates values of type String.
	 *
	 * @return a new arbitrary instance
	 */
	public static StringArbitrary strings() {
		return ArbitrariesFacade.implementation.strings();
	}

	/**
	 * Create an arbitrary that generates values of type Character.
	 *
	 * @return a new arbitrary instance
	 */
	public static CharacterArbitrary chars() {
		return ArbitrariesFacade.implementation.chars().all();
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
		return fromGenerators(
			ArbitrariesFacade.implementation.randomSamples(samples),
			ArbitrariesFacade.implementation.exhaustiveChoose(Arrays.asList(samples))
		);
	}

	/**
	 * Create an arbitrary that will always generate the same value.
	 *
	 * @param value The value to "generate"
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> constant(T value) {
		return fromGenerators(
			random -> Shrinkable.unshrinkable(value),
			ArbitrariesFacade.implementation.exhaustiveChoose(Arrays.asList(value))
		);
	}

	/**
	 * Create an arbitrary that will always generate a list which is a
	 * permutation of the values handed to it. Permutations will
	 * not be shrunk.
	 *
	 * @param values The values to permute
	 * @param <T>    The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<List<T>> shuffle(T... values) {
		return shuffle(Arrays.asList(values));
	}

	/**
	 * Create an arbitrary that will always generate a list which is a
	 * permutation of the values handed to it. Permutations will
	 * not be shrunk.
	 *
	 * @param values The values to permute
	 * @param <T>    The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<List<T>> shuffle(List<T> values) {
		return fromGenerators(
			ArbitrariesFacade.implementation.randomShuffle(values),
			ArbitrariesFacade.implementation.exhaustiveShuffle(values)
		);
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
		return ArbitrariesFacade.implementation.defaultFor(type, typeParameters);
	}

	private static <T> Arbitrary<T> fromGenerators(
		RandomGenerator<T> randomGenerator,
		Optional<ExhaustiveGenerator<T>> exhaustiveGenerator
	) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return randomGenerator;
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive() {
				return exhaustiveGenerator;
			}
		};
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
	 * Create an arbitrary by deterministic recursion.
	 *
	 * This is useful (and necessary) when arbitrary providing functions use other arbitrary providing functions
	 * in a recursive way. Without the use of lazy() this would result in a stack overflow.
	 *
	 * @param base The supplier returning the recursion's base case
	 * @param recur The function to extend the base case
	 * @param depth The number of times to invoke recursion
	 * @param <T> The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> recursive(
		Supplier<Arbitrary<T>> base,
		Function<Arbitrary<T>, Arbitrary<T>> recur,
		int depth
	) {
		if (depth == 0) {
			return base.get();
		}
		return recur.apply(recursive(base, recur, depth - 1));
	}

	/**
	 * Create an arbitrary to create a sequence of actions. Useful for stateful testing.
	 *
	 * @param actionArbitrary The arbitrary to generate individual actions.
	 * @param <M> The type of actions to generate
	 * @return a new arbitrary instance
	 */
	public static <M> ActionSequenceArbitrary<M> sequences(Arbitrary<Action<M>> actionArbitrary) {
		return ArbitrariesFacade.implementation.sequences(actionArbitrary);
	}

}
