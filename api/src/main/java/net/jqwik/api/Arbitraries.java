package net.jqwik.api;

import javax.annotation.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.arbitraries.TraverseArbitrary.*;
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

		public abstract <T> EdgeCases<T> edgeCasesChoose(List<T> values, int maxEdgeCases);

		public abstract <T> Optional<ExhaustiveGenerator<T>> exhaustiveChoose(List<T> values, long maxNumberOfSamples);

		public abstract <T> Optional<ExhaustiveGenerator<T>> exhaustiveCreate(Supplier<T> supplier, long maxNumberOfSamples);

		public abstract <T> Optional<ExhaustiveGenerator<List<T>>> exhaustiveShuffle(List<T> values, long maxNumberOfSamples);

		public abstract <T> Arbitrary<T> oneOf(Collection<Arbitrary<? extends T>> all);

		public abstract <T> RandomGenerator<T> randomFrequency(List<Tuple2<Integer, T>> frequencies);

		public abstract <T> RandomGenerator<List<T>> randomShuffle(List<T> values);

		public abstract <M> ActionSequenceArbitrary<M> sequences(Arbitrary<? extends Action<M>> actionArbitrary);

		public abstract <T> Arbitrary<T> frequencyOf(List<Tuple2<Integer, Arbitrary<T>>> frequencies);

		public abstract <@NullableType T> Arbitrary<T> just(@Nullable T value);

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

		public abstract <T> Arbitrary<T> defaultFor(TypeUsage typeUsage, Function<TypeUsage, Arbitrary<Object>> noDefaultResolver);

		public abstract <T> Arbitrary<T> lazy(Supplier<Arbitrary<T>> arbitrarySupplier);

		public abstract <T> TypeArbitrary<T> forType(Class<T> targetType);

		public abstract <K, V> MapArbitrary<K, V> maps(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary);

		public abstract <K, V> Arbitrary<Map.Entry<K, V>> entries(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary);

		public abstract <T> Arbitrary<T> recursive(
			Supplier<Arbitrary<T>> base,
			Function<Arbitrary<T>, Arbitrary<T>> recur,
			int minDepth,
			int maxDepth
		);

		public abstract <T> Arbitrary<T> lazyOf(List<Supplier<Arbitrary<T>>> suppliers);

		public abstract <T> TraverseArbitrary<T> traverse(Class<T> targetType, Traverser traverser);

		public abstract Arbitrary<Character> of(char[] chars);

		public abstract <T> Arbitrary<T> of(Collection<T> values);
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
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
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
		return ArbitrariesFacade.implementation.of(values);
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
	 * @param <T>            The type of values to generate
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
	 * @param <T>            The type of values to generate
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
		return ArbitrariesFacade.implementation.of(values);
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
	public static <T> Arbitrary<T> oneOf(Arbitrary<? extends T> first, Arbitrary<? extends T>... rest) {
		List<Arbitrary<? extends T>> all = new ArrayList<>();
		all.add(first);
		for (Arbitrary<?> arbitrary : rest) {
			all.add((Arbitrary<? extends T>) arbitrary);
		}
		return oneOf(all);
	}

	/**
	 * Create an arbitrary that will randomly choose between all given arbitraries of the same type T.
	 *
	 * @param choices A collection of arbitraries to choose from
	 * @param <T>     The type of values to generate
	 * @return a new arbitrary instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> Arbitrary<T> oneOf(Collection<Arbitrary<? extends T>> choices) {
		if (choices.isEmpty()) {
			String message = "oneOf() must not be called with no choices";
			throw new JqwikException(message);
		}
		if (choices.size() == 1) {
			return (Arbitrary<T>) choices.iterator().next();
		}
		// Simple flatMapping is not enough because of configurations
		return ArbitrariesFacade.implementation.oneOf(choices);
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
			maxEdgeCases -> ArbitrariesFacade.implementation.edgeCasesChoose(values, maxEdgeCases)
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
	 * Create an arbitrary that will always generate the same value.
	 *
	 * @param value The value to "generate".
	 * @param <T>   The type of the value
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.2")
	public static <@NullableType T> Arbitrary<T> just(@Nullable T value) {
		return ArbitrariesFacade.implementation.just(value);
	}

	/**
	 * Create an arbitrary that will use a supplier to generate a value.
	 * The difference to {@linkplain Arbitraries#just(Object)} is that the value
	 * is freshly generated for each try of a property.
	 *
	 * <p>
	 * Mind that within a {@code supplier} you should never use other arbitraries
	 * or do anything non-deterministic.
	 * </p>
	 *
	 * <p>
	 * For exhaustive shrinking all generated values are supposed to have identical behaviour,
	 * i.e. that means that only one value is generated per combination.
	 * </p>
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
			maxEdgeCases -> EdgeCases.fromSupplier(() -> Shrinkable.supplyUnshrinkable(supplier))
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
			maxEdgeCases -> EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(values))
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
	 * <p>The returned arbitrary is lazy, i.e. it be evaluated at generation time to allow
	 * domain contexts to be used.</p>
	 *
	 * @param typeUsage The type of the value to find an arbitrary for
	 * @param <T>       The type of values to generate
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.1")
	public static <T> Arbitrary<T> defaultFor(TypeUsage typeUsage) {
		return defaultFor(typeUsage, ignore -> {throw new CannotFindArbitraryException(typeUsage);});
	}

	/**
	 * Find a registered arbitrary that will be used to generate values of type T.
	 * All default arbitrary providers and all registered arbitrary providers are considered.
	 * This is more or less the same mechanism that jqwik uses to find arbitraries for
	 * property method parameters.
	 *
	 * <p>The returned arbitrary is lazy, i.e. it be evaluated at generation time to allow
	 * domain contexts to be used. Those </p>
	 *
	 * @param typeUsage         The type of the value to find an arbitrary for
	 * @param noDefaultResolver Alternative resolution when no default arbitrary can be found at generation time
	 * @param <T>               The type of values to generate
	 * @return a new arbitrary instance
	 */
	@API(status = EXPERIMENTAL, since = "1.6.1")
	public static <T> Arbitrary<T> defaultFor(TypeUsage typeUsage, Function<TypeUsage, Arbitrary<Object>> noDefaultResolver) {
		return ArbitrariesFacade.implementation.defaultFor(typeUsage, noDefaultResolver);
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

	/**
	 * Create an arbitrary for type {@code T} that will try to traverse a type
	 * - and all types it is based on - given a {@linkplain Traverser} strategy.
	 *
	 * <p>
	 * By default recursion is disable, i.e. that parameters of creator functions
	 * can be resolved by the traverser, have a default arbitrary or generation fails at runtime.
	 * Use {@linkplain TraverseArbitrary#enableRecursion()} to switch on recursive traversal.
	 * </p>
	 * <p>
	 * One usage of this traversing mechanism is {@linkplain #forType(Class)}
	 * which uses {@linkplain #traverse(Class, Traverser)} under the hood.
	 * </p>
	 *
	 * @param targetType The class of the type to create an arbitrary for
	 * @param <T>        The type of values to generate
	 * @param traverser  The traversing strategy specification
	 * @return a new arbitrary instance
	 * @see TraverseArbitrary
	 */
	@API(status = EXPERIMENTAL, since = "1.6.1")
	public static <T> TraverseArbitrary<T> traverse(Class<T> targetType, Traverser traverser) {
		return ArbitrariesFacade.implementation.traverse(targetType, traverser);
	}

	// TODO: Get rid of all callers
	private static <T> Arbitrary<T> fromGenerators(
		final RandomGenerator<T> randomGenerator,
		final Function<Long, Optional<ExhaustiveGenerator<T>>> exhaustiveGeneratorFunction,
		final Function<Integer, EdgeCases<T>> edgeCasesSupplier
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
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				return maxEdgeCases <= 0
						   ? EdgeCases.none()
						   : edgeCasesSupplier.apply(maxEdgeCases);
			}
		};
	}

	/**
	 * Create an arbitrary that will evaluate arbitrarySupplier as soon as it is used for generating values.
	 * <p>
	 * This is useful (and necessary) when arbitrary providing functions use other arbitrary providing functions
	 * in a recursive way. Without the use of lazy() this would result in a stack overflow.
	 * Most of the time, however, using {@linkplain #lazyOf(Supplier, Supplier[])} is the better choice
	 * because it has significantly better shrinking behaviour.
	 *
	 * @param arbitrarySupplier The supplier function being used to generate an arbitrary
	 * @param <T>               The type of values to generate
	 * @return a new arbitrary instance
	 * @see #recursive(Supplier, Function, int)
	 * @see #lazyOf(Supplier, Supplier[])
	 */
	public static <T> Arbitrary<T> lazy(Supplier<Arbitrary<T>> arbitrarySupplier) {
		return ArbitrariesFacade.implementation.lazy(arbitrarySupplier);
	}

	/**
	 * Create an arbitrary by deterministic recursion.
	 * <p>
	 * Mind that the arbitrary will be created by invoking recursion at arbitrary creation time.
	 * Using {@linkplain #lazyOf(Supplier, Supplier[])} or {@linkplain #lazy(Supplier)} instead
	 * will recur at value generation time.
	 *
	 * @param base  The supplier returning the recursion's base case
	 * @param recur The function to extend the base case
	 * @param depth The number of times to invoke recursion
	 * @param <T>   The type of values to generate
	 * @return a new arbitrary instance
	 * @see #lazy(Supplier)
	 */
	public static <T> Arbitrary<T> recursive(
		Supplier<Arbitrary<T>> base,
		Function<Arbitrary<T>, Arbitrary<T>> recur,
		int depth
	) {
		return ArbitrariesFacade.implementation.recursive(base, recur, depth, depth);
	}

	/**
	 * Create an arbitrary by deterministic recursion.
	 * <p>
	 * Mind that the arbitrary will be created by invoking recursion at arbitrary creation time.
	 * Using {@linkplain #lazyOf(Supplier, Supplier[])} or {@linkplain #lazy(Supplier)} instead
	 * will recur at value generation time.
	 *
	 * @param base     The supplier returning the recursion's base case
	 * @param recur    The function to extend the base case
	 * @param minDepth The minimum number of times to invoke recursion
	 * @param maxDepth The maximum number of times to invoke recursion
	 * @param <T>      The type of values to generate
	 * @return a new arbitrary instance
	 * @see #lazy(Supplier)
	 */
	@API(status = MAINTAINED, since = "1.6.4")
	public static <T> Arbitrary<T> recursive(
		Supplier<Arbitrary<T>> base,
		Function<Arbitrary<T>, Arbitrary<T>> recur,
		int minDepth,
		int maxDepth
	) {
		return ArbitrariesFacade.implementation.recursive(base, recur, minDepth, maxDepth);
	}

	/**
	 * Create an arbitrary by lazy supplying one of several arbitraries.
	 * The main use of this function is to allow recursive generation of structured
	 * values without overflowing the stack.
	 *
	 * <p>
	 * One alternative is to use {@linkplain #lazy(Supplier)} combined with
	 * {@linkplain Arbitraries#oneOf(Arbitrary, Arbitrary[])}
	 * or {@linkplain Arbitraries#frequencyOf(Tuple.Tuple2[])}.
	 * But {@code lazyOf()} has considerably better shrinking behaviour with recursion.
	 * </p>
	 *
	 * <p>
	 * <em>Caveat:</em>
	 * Never use this construct if suppliers make use of variable state
	 * like method parameters or changing instance members.
	 * In those cases use {@linkplain #lazy(Supplier)} instead.
	 * </p>
	 *
	 * @param first The first supplier to choose from
	 * @param rest  The rest of suppliers to choose from
	 * @param <T>   The type of values to generate
	 * @return a (potentially cached) arbitrary instance
	 * @see #lazy(Supplier)
	 * @see #recursive(Supplier, Function, int)
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	@API(status = MAINTAINED, since = "1.3.4")
	public static <T> Arbitrary<T> lazyOf(Supplier<Arbitrary<? extends T>> first, Supplier<Arbitrary<? extends T>>... rest) {
		List<Supplier<Arbitrary<T>>> all = new ArrayList<>();
		all.add(() -> (Arbitrary<T>) first.get());
		for (Supplier<Arbitrary<? extends T>> arbitrarySupplier : rest) {
			all.add(() -> (Arbitrary<T>) arbitrarySupplier.get());
		}
		return ArbitrariesFacade.implementation.lazyOf(all);
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
	 * @param <K>             type of keys
	 * @param <V>             type of values
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.1.6")
	public static <K, V> MapArbitrary<K, V> maps(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		return ArbitrariesFacade.implementation.maps(keysArbitrary, valuesArbitrary);
	}

	/**
	 * Create an arbitrary to create instances of {@linkplain java.util.Map.Entry}.
	 * The generated entries are mutable.
	 *
	 * @param keysArbitrary   The arbitrary to generate the keys
	 * @param valuesArbitrary The arbitrary to generate the values
	 * @param <K>             type of keys
	 * @param <V>             type of values
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

	/**
	 * Create a new arbitrary of element type {@code Set<T>} using the handed in values as elements of the set.
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.6.4")
	public static <T> SetArbitrary<T> subsetOf(Collection<T> values) {
		return of(values).set();
	}

	/**
	 * Create a new arbitrary of element type {@code Set<T>} using the handed in values as elements of the set.
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.6.4")
	public static <T> SetArbitrary<T> subsetOf(T... values) {
		return subsetOf(Arrays.asList(values));
	}

}
