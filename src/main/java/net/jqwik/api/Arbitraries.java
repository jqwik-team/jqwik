package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.ArbitraryProvider.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.properties.arbitraries.exhaustive.*;
import net.jqwik.properties.arbitraries.randomized.*;
import net.jqwik.properties.stateful.*;
import net.jqwik.providers.*;

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
		return fromGenerators(RandomGenerators.choose(values), ExhaustiveGenerators.choose(values));
	}

	/**
	 * Create an arbitrary of character values.
	 *
	 * @param values The array of characters to choose from.
	 * @return a new arbitrary instance
	 */
	public static Arbitrary<Character> of(char[] values) {
		return fromGenerators(RandomGenerators.choose(values), ExhaustiveGenerators.choose(values));
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
		return fromGenerators(RandomGenerators.choose(enumClass), ExhaustiveGenerators.choose(enumClass));
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
		return new OneOfArbitrary<>(all);
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
	 * Create an arbitrary that generates values of type Long.
	 *
	 * @return a new arbitrary instance
	 */
	public static LongArbitrary longs() {
		return new DefaultLongArbitrary();
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
	 * Create an arbitrary that generates values of type Float.
	 *
	 * @return a new arbitrary instance
	 */
	public static FloatArbitrary floats() {
		return new DefaultFloatArbitrary();
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
	 * Create an arbitrary that generates values of type Double.
	 *
	 * @return a new arbitrary instance
	 */
	public static DoubleArbitrary doubles() {
		return new DefaultDoubleArbitrary();
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
	 * Create an arbitrary that generates values of type Short.
	 *
	 * @return a new arbitrary instance
	 */
	public static ShortArbitrary shorts() {
		return new DefaultShortArbitrary();
	}

	/**
	 * Create an arbitrary that generates values of type String.
	 *
	 * @return a new arbitrary instance
	 */
	public static StringArbitrary strings() {
		return new DefaultStringArbitrary();
	}

	public static CharacterArbitrary chars() {
		return new DefaultCharacterArbitrary().all();
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
		return new Arbitrary<List<T>>() {
			@Override
			public RandomGenerator<List<T>> generator(int genSize) {
				return RandomGenerators.shuffle(values);
			}

			@Override
			public Optional<ExhaustiveGenerator<List<T>>> exhaustive() {
				return Optional.empty();
			}
		};
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
		return oneOfAllDefaults(TypeUsage.of(type, genericTypeParameters));
	}

	private static <T> Arbitrary<T> oneOfAllDefaults(TypeUsage typeUsage) {
		Set<Arbitrary<?>> arbitraries = allDefaultsFor(typeUsage);
		if (arbitraries.isEmpty()) {
			throw new CannotFindArbitraryException(typeUsage);
		}

		List<Arbitrary<T>> arbitrariesList = new ArrayList<>();
		//noinspection unchecked
		arbitraries.forEach(arbitrary -> arbitrariesList.add((Arbitrary<T>) arbitrary));
		return oneOf(arbitrariesList);
	}

	private static Set<Arbitrary<?>> allDefaultsFor(TypeUsage typeUsage) {
		RegisteredArbitraryResolver defaultArbitraryResolver =
			new RegisteredArbitraryResolver(RegisteredArbitraryProviders.getProviders());
		SubtypeProvider subtypeProvider = Arbitraries::allDefaultsFor;
		return defaultArbitraryResolver.resolve(typeUsage, subtypeProvider);
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
