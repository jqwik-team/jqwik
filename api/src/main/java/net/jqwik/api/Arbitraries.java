package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.stateful.*;

import static org.apiguardian.api.API.Status.*;

@API(status = STABLE, since = "1.0")
public class Arbitraries {

	@API(status = INTERNAL)
	public static abstract class ArbitrariesFacade {
		private static final ArbitrariesFacade implementation;

		static {
			implementation = FacadeLoader.load(ArbitrariesFacade.class);
		}

		public abstract <T> EdgeCases<T> edgeCasesChoose(List<T> values);

		public abstract <T> EdgeCases<T> edgeCasesChoose(char[] validChars);

		public abstract <T> Optional<ExhaustiveGenerator<T>> exhaustiveChoose(List<T> values, long maxNumberOfSamples);

		public abstract <T> Optional<ExhaustiveGenerator<T>> exhaustiveCreate(Supplier<T> supplier, long maxNumberOfSamples);

		public abstract Optional<ExhaustiveGenerator<Character>> exhaustiveChoose(char[] values, long maxNumberOfSamples);

		public abstract <T> Optional<ExhaustiveGenerator<List<T>>> exhaustiveShuffle(List<T> values, long maxNumberOfSamples);

		public abstract <T> RandomGenerator<T> randomChoose(List<T> values);

		public abstract RandomGenerator<Character> randomChoose(char[] values);

		public abstract <T> Arbitrary<T> oneOf(List<Arbitrary<T>> all);

		public abstract <T> RandomGenerator<T> randomFrequency(List<Tuple2<Integer, T>> frequencies);

		public abstract <T> RandomGenerator<T> randomSamples(T[] samples);

		public abstract <T> RandomGenerator<List<T>> randomShuffle(List<T> values);

		public abstract <M> ActionSequenceArbitrary<M> sequences(Arbitrary<? extends Action<M>> actionArbitrary);

		public abstract <T> Arbitrary<T> frequencyOf(List<Tuple2<Integer, Arbitrary<T>>> frequencies);

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

		public abstract <T> Arbitrary<T> defaultFor(TypeUsage typeUsage);

		public abstract <T> Arbitrary<T> lazy(Supplier<Arbitrary<T>> arbitrarySupplier);

		public abstract <T> TypeArbitrary<T> forType(Class<T> targetType);

		public abstract <K, V> MapArbitrary<K, V> maps(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary);

		public abstract <K, V> Arbitrary<Map.Entry<K, V>> entries(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary);

		public abstract <T> Arbitrary<T> recursive(Supplier<Arbitrary<T>> base, Function<Arbitrary<T>, Arbitrary<T>> recur, int depth);
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
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(final int genSize) {
				return generator;
			}

			@Override
			public EdgeCases<T> edgeCases() {
				return EdgeCases.none();
			}
		};
	}

	/**
	 * Create an arbitrary that will generate values of type T using a generator function.
	 * The generated values are unshrinkable.
	 *
	 * @param generator The generator function to be used for generating the values
	 * @param <T>       The type of values to generate
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
	 * <p>
	 * Use this method only for immutable values, because changing the value will change
	 * subsequent generated values as well.
	 * For mutable values use {@linkplain #ofSuppliers(Supplier[])} instead.
	 *
	 * @param values The array of values to choose from
	 * @param <T>    The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SafeVarargs
	public static <T> Arbitrary<T> of(T... values) {
		return of(Arrays.asList(values));
	}

	/**
	 * Create an arbitrary that will randomly choose from a given collection of values.
	 * A generated value will be shrunk towards the start of the collection.
	 *
	 * <p>
	 * Use this method only for immutable values, because changing the value will change
	 * subsequent generated values as well.
	 * For mutable values use {@linkplain #ofSuppliers(Collection)} instead.
	 * 
	 * @param values The collection of values to choose from
	 * @param <T>    The type of values to generate
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.1")
	public static <T> Arbitrary<T> of(Collection<T> values) {
		List<T> valueList = values instanceof List ? (List<T>) values :  new ArrayList<>(values);
		return fromGenerators(
			ArbitrariesFacade.implementation.randomChoose(valueList),
			max -> ArbitrariesFacade.implementation.exhaustiveChoose(valueList, max),
			ArbitrariesFacade.implementation.edgeCasesChoose(valueList)
		);
	}

	/**
	 * Create an arbitrary that will randomly choose from a given array of value suppliers
	 * and then get the value from the supplier.
	 * A generated value will be shrunk towards the start of the array.
	 *
	 * <p>
	 * Use this method instead of {@linkplain #of(Object[])} for mutable objects
	 * to make sure that changing a generated object will not influence other generated
	 * objects.
	 *
	 * @param valueSuppliers The array of values to choose from
	 * @param <T>    The type of values to generate
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	@SafeVarargs
	public static <T> Arbitrary<T> ofSuppliers(Supplier<T>... valueSuppliers) {
		return of(valueSuppliers).map(Supplier::get);
	}

	/**
	 * Create an arbitrary that will randomly choose from a given collection of value suppliers
	 * and then get the value from the supplier.
	 * A generated value will be shrunk towards the start of the collection.
	 *
	 * <p>
	 * Use this method instead of {@linkplain #of(Collection)} for mutable objects
	 * to make sure that changing a generated object will not influence other generated
	 * objects.
	 *
	 * @param valueSuppliers The collection of values to choose from
	 * @param <T>    The type of values to generate
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.1")
	public static <T> Arbitrary<T> ofSuppliers(Collection<Supplier<T>> valueSuppliers) {
		return of(valueSuppliers).map(Supplier::get);
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
			max -> ArbitrariesFacade.implementation.exhaustiveChoose(values, max),
			ArbitrariesFacade.implementation.edgeCasesChoose(values)
		);
	}

	/**
	 * Create an arbitrary for enum values of type T.
	 *
	 * @param enumClass The enum class.
	 * @param <T>       The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T extends Enum<T>> Arbitrary<T> of(Class<T> enumClass) {
		List<T> values = Arrays.asList(enumClass.getEnumConstants());
		return of(values);
	}

	/**
	 * Create an arbitrary that will randomly choose between all given arbitraries of the same type T.
	 *
	 * @param first The first arbitrary to choose form
	 * @param rest  An array of arbitraries to choose from
	 * @param <T>   The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> Arbitrary<T> oneOf(Arbitrary<? extends T> first, Arbitrary<? extends T>... rest) {
		List<Arbitrary<T>> all = new ArrayList<>();
		all.add((Arbitrary<T>) first);
		for (Arbitrary<?> arbitrary : rest) {
			all.add((Arbitrary<T>) arbitrary);
		}
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
	 * @param <T>         The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SafeVarargs
	public static <T> Arbitrary<T> frequency(Tuple2<Integer, T>... frequencies) {
		return frequency(Arrays.asList(frequencies));
	}

	/**
	 * Create an arbitrary that will randomly choose between all given values of the same type T.
	 * The probability distribution is weighted with the first parameter of the tuple.
	 *
	 * @param frequencies A list of tuples of which the first parameter gives the weight and the second the value.
	 * @param <T>         The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> frequency(List<Tuple2<Integer, T>> frequencies) {
		List<T> values = frequencies.stream()
									.filter(f -> f.get1() > 0)
									.map(Tuple2::get2)
									.collect(Collectors.toList());

		return fromGenerators(
			ArbitrariesFacade.implementation.randomFrequency(frequencies),
			max -> ArbitrariesFacade.implementation.exhaustiveChoose(values, max),
			ArbitrariesFacade.implementation.edgeCasesChoose(values)
		);

	}

	/**
	 * Create an arbitrary that will randomly choose between all given arbitraries of the same type T.
	 * The probability distribution is weighted with the first parameter of the tuple.
	 *
	 * @param frequencies An array of tuples of which the first parameter gives the weight and the second the arbitrary.
	 * @param <T>         The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> Arbitrary<T> frequencyOf(Tuple2<Integer, Arbitrary<? extends T>>... frequencies) {
		List<Tuple2<Integer, Arbitrary<T>>> all = new ArrayList<>();
		for (Tuple2<Integer, Arbitrary<? extends T>> frequency : frequencies) {
			all.add(Tuple.of(frequency.get1(), (Arbitrary<T>) frequency.get2()));
		}
		return frequencyOf(all);
	}

	/**
	 * Create an arbitrary that will randomly choose between all given arbitraries of the same type T.
	 * The probability distribution is weighted with the first parameter of the tuple.
	 *
	 * @param frequencies A list of tuples of which the first parameter gives the weight and the second the arbitrary.
	 * @param <T>         The type of values to generate
	 * @return a new arbitrary instance
	 */
	public static <T> Arbitrary<T> frequencyOf(List<Tuple2<Integer, Arbitrary<T>>> frequencies) {
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
		return ArbitrariesFacade.implementation.chars();
	}

	/**
	 * Create an arbitrary that will provide the sample values from first to last
	 * and then start again at the beginning. Shrinking of samples is tried
	 * towards the start of the samples.
	 *
	 * <p>
	 * Attention: If you want to randomly choose between {@code samples}
	 * you must use {@link Arbitraries#of(Object[])}
	 * </p>
	 *
	 * @param samples The array of sample values
	 * @param <T>     The type of values to generate
	 * @return a new arbitrary instance
	 * @deprecated Use {@link Arbitraries#of(Object[])} or move to data-driven properties if order is important. Will be removed in version 1.4.0
	 */
	@SafeVarargs
	@Deprecated
	@API(status = DEPRECATED, since = "1.3.0")
	public static <T> Arbitrary<T> samples(T... samples) {
		return fromGenerators(
			ArbitrariesFacade.implementation.randomSamples(samples),
			max -> ArbitrariesFacade.implementation.exhaustiveChoose(Arrays.asList(samples), max),
			EdgeCases.none()
		);
	}

	/**
	 * Create an arbitrary that will always generate the same value.
	 *
	 * @param value The value to "generate"
	 * @param <T>   The type of the value
	 * @return a new arbitrary instance
	 *
	 * @see #just(Object)
	 *
	 * @deprecated Use {@linkplain Arbitraries#just(Object)} instead. To be removed in version 2.0.
	 *
	 **/
	@API(status = DEPRECATED, since = "1.3.2")
	public static <T> Arbitrary<T> constant(T value) {
		return just(value);
	}

	/**
	 * Create an arbitrary that will always generate the same value.
	 *
	 * @param value The value to "generate"
	 * @param <T>   The type of the value
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.2")
	public static <T> Arbitrary<T> just(T value) {
		return fromGenerators(
			random -> Shrinkable.unshrinkable(value),
			max -> ArbitrariesFacade.implementation.exhaustiveChoose(Arrays.asList(value), max),
			EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(value))
		);
	}

	/**
	 * Create an arbitrary that will use a supplier to generate a value.
	 * The difference to {@linkplain Arbitraries#just(Object)} is that the value
	 * is freshly generated for each try of a property.
	 * <p>
	 * For exhaustive shrinking all generated values are supposed to have identical behaviour,
	 * i.e. that means that only one value is generated per combination.
	 *
	 * @param supplier The supplier use to generate a value
	 * @param <T>      The type of values to generate
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.1.1")
	public static <T> Arbitrary<T> create(Supplier<T> supplier) {
		return fromGenerators(
			random -> Shrinkable.supplyUnshrinkable(supplier),
			max -> ArbitrariesFacade.implementation.exhaustiveCreate(supplier, max),
			EdgeCases.fromSupplier(() -> Shrinkable.supplyUnshrinkable(supplier))
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
	@SafeVarargs
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
			max -> ArbitrariesFacade.implementation.exhaustiveShuffle(values, max),
			EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(values))
		);
	}

	/**
	 * Find a registered arbitrary that will be used to generate values of type T.
	 * All default arbitrary providers and all registered arbitrary providers are considered.
	 * This is more or less the same mechanism that jqwik uses to find arbitraries for
	 * property method parameters.
	 *
	 * @param type           The type of the value to find an arbitrary for
	 * @param typeParameters The type parameters if type is a generic type
	 * @param <T>            The type of values to generate
	 * @return a new arbitrary instance
	 * @throws CannotFindArbitraryException if there is no registered arbitrary provider to serve this type
	 */
	public static <T> Arbitrary<T> defaultFor(Class<T> type, Class<?>... typeParameters) {
		return ArbitrariesFacade.implementation.defaultFor(type, typeParameters);
	}

	/**
	 * Find a registered arbitrary that will be used to generate values of type T.
	 * All default arbitrary providers and all registered arbitrary providers are considered.
	 * This is more or less the same mechanism that jqwik uses to find arbitraries for
	 * property method parameters.
	 *
	 * @param typeUsage The type of the value to find an arbitrary for
	 * @param <T>       The type of values to generate
	 * @return a new arbitrary instance
	 * @throws CannotFindArbitraryException if there is no registered arbitrary provider to serve this type
	 */
	@API(status = MAINTAINED, since = "1.1")
	public static <T> Arbitrary<T> defaultFor(TypeUsage typeUsage) {
		return ArbitrariesFacade.implementation.defaultFor(typeUsage);
	}

	/**
	 * Create an arbitrary for type {@code T} that will by default use the type's
	 * public constructors and public factory methods.
	 *
	 * @param targetType The class of the type to create an arbitrary for
	 * @param <T>        The type of values to generate
	 * @return a new arbitrary instance
	 * @see TypeArbitrary
	 */
	@API(status = MAINTAINED, since = "1.2.0")
	public static <T> TypeArbitrary<T> forType(Class<T> targetType) {
		return ArbitrariesFacade.implementation.forType(targetType);
	}

	private static <T> Arbitrary<T> fromGenerators(
		RandomGenerator<T> randomGenerator,
		Function<Long, Optional<ExhaustiveGenerator<T>>> exhaustiveGeneratorFunction,
		final EdgeCases<T> edgeCases
	) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return randomGenerator;
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return exhaustiveGeneratorFunction.apply(maxNumberOfSamples);
			}

			@Override
			public EdgeCases<T> edgeCases() {
				return edgeCases;
			}
		};
	}

	/**
	 * Create an arbitrary that will evaluate arbitrarySupplier as soon as it is used for generating values.
	 * <p>
	 * This is useful (and necessary) when arbitrary providing functions use other arbitrary providing functions
	 * in a recursive way. Without the use of lazy() this would result in a stack overflow.
	 *
	 * @param arbitrarySupplier The supplier function being used to generate an arbitrary
	 * @param <T>               The type of values to generate
	 * @return a new arbitrary instance
	 * 
	 * @see #recursive(Supplier, Function, int)
	 */
	public static <T> Arbitrary<T> lazy(Supplier<Arbitrary<T>> arbitrarySupplier) {
		return ArbitrariesFacade.implementation.lazy(arbitrarySupplier);
	}

	/**
	 * Create an arbitrary by deterministic recursion.
	 * <p>
	 * Mind that the arbitrary will be created by invoking recursion at arbitrary creation time.
	 * Using {@linkplain #lazy(Supplier)} instead will recur at value generation time.
	 *
	 * @param base  The supplier returning the recursion's base case
	 * @param recur The function to extend the base case
	 * @param depth The number of times to invoke recursion
	 * @param <T>   The type of values to generate
	 * @return a new arbitrary instance
	 * 
	 * @see #lazy(Supplier) 
	 */
	public static <T> Arbitrary<T> recursive(
		Supplier<Arbitrary<T>> base,
		Function<Arbitrary<T>, Arbitrary<T>> recur,
		int depth
	) {
		return ArbitrariesFacade.implementation.recursive(base, recur, depth);
	}

	@SafeVarargs
	@API(status = EXPERIMENTAL, since = "1.3.4")
	public static <T> Arbitrary<T> lazyOf(Supplier<Arbitrary<T>> ... arbitrarySuppliers) {
		// TODO: Use your own implementation
		List<Arbitrary<T>> arbitraries = Arrays.stream(arbitrarySuppliers).map(Arbitraries::lazy).collect(Collectors.toList());
		return Arbitraries.oneOf(arbitraries);
	}

	/**
	 * Create an arbitrary to create a sequence of actions. Useful for stateful testing.
	 *
	 * @param actionArbitrary The arbitrary to generate individual actions.
	 * @param <M>             The type of actions to generate
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.0")
	public static <M> ActionSequenceArbitrary<M> sequences(Arbitrary<? extends Action<M>> actionArbitrary) {
		return ArbitrariesFacade.implementation.sequences(actionArbitrary);
	}

	/**
	 * Create an arbitrary to create instances of {@linkplain Map}.
	 * The generated maps are mutable.
	 *
	 * @param keysArbitrary   The arbitrary to generate the keys
	 * @param valuesArbitrary The arbitrary to generate the values
	 * @param <K> type of keys
	 * @param <V> type of values
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.1.6")
	public static <K, V> MapArbitrary<K, V> maps(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		return ArbitrariesFacade.implementation.maps(keysArbitrary, valuesArbitrary);
	}

	/**
	 * Create an arbitrary to create instances of {@linkplain Map.Entry}.
	 * The generated entries are mutable.
	 *
	 * @param keysArbitrary   The arbitrary to generate the keys
	 * @param valuesArbitrary The arbitrary to generate the values
	 * @param <K> type of keys
	 * @param <V> type of values
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.2.0")
	public static <K, V> Arbitrary<Map.Entry<K, V>> entries(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		return ArbitrariesFacade.implementation.entries(keysArbitrary, valuesArbitrary);
	}

	/**
	 * Create an arbitrary that never creates anything. Sometimes useful
	 * when generating arbitraries of "functions" that have void as return type.
	 *
	 * @return arbitrary instance that will generate nothing
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	public static Arbitrary<Void> nothing() {
		return just(null);
	}

}
