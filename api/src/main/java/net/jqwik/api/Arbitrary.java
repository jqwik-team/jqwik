package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The main interface for representing objects that can be generated and shrunk.
 *
 * @param <T> The type of generated objects. Primitive objects (e.g. int, boolean etc.) are represented by their boxed
 *            type (e.g. Integer, Boolean).
 */
@API(status = STABLE, since = "1.0")
public interface Arbitrary<T> {

	@API(status = INTERNAL)
	abstract class ArbitraryFacade {
		private static final ArbitraryFacade implementation;

		static {
			implementation = FacadeLoader.load(ArbitraryFacade.class);
		}

		public abstract <T, U> Optional<ExhaustiveGenerator<U>> flatMapExhaustiveGenerator(
			ExhaustiveGenerator<T> self,
			Function<T, Arbitrary<U>> mapper,
			long maxNumberOfSamples
		);

		public abstract <T> StreamableArbitrary<T, List<T>> list(Arbitrary<T> elementArbitrary);

		public abstract <T> SetArbitrary<T> set(Arbitrary<T> elementArbitrary);

		public abstract <T> StreamableArbitrary<T, Stream<T>> stream(Arbitrary<T> elementArbitrary);

		public abstract <T> StreamableArbitrary<T, Iterator<T>> iterator(Arbitrary<T> elementArbitrary);

		public abstract <T, A> StreamableArbitrary<T, A> array(Arbitrary<T> elementArbitrary, Class<A> arrayClass);

		public abstract <T> Stream<T> sampleStream(Arbitrary<T> arbitrary);
	}

	/**
	 * Create the random generator for an arbitrary
	 *
	 * @param genSize a very unspecific configuration parameter that can be used
	 *                to influence the configuration and behaviour of a random generator
	 *                if and only if the generator wants to be influenced.
	 *                Many generators are independent of genSize.
	 *                <p>
	 *                The default value of {@code genSize} is the number of tries configured
	 *                for a property. Use {@linkplain Arbitrary#fixGenSize(int)} to fix
	 *                the parameter for a given arbitrary.
	 * @return a new random generator instance
	 */
	RandomGenerator<T> generator(int genSize);

	/**
	 * Sometimes simplifies test writing
	 */
	@SuppressWarnings("unchecked")
	@API(status = INTERNAL)
	default Arbitrary<Object> asGeneric() {
		return (Arbitrary<Object>) this;
	}

	/**
	 * All arbitraries whose base generator is supposed to produce no duplicates
	 * should return true.
	 *
	 * @return true if base genator is supposed to produce no duplicates
	 */
	@API(status = INTERNAL)
	default boolean isUnique() {
		return false;
	}

	/**
	 * Create the exhaustive generator for an arbitrary using the maximum allowed
	 * number of generated samples. Just trying to find out if such a generator
	 * exists might take a long time. This method should never be overridden.
	 *
	 * @return a new exhaustive generator or Optional.empty() if it cannot be created.
	 */
	@API(status = INTERNAL)
	default Optional<ExhaustiveGenerator<T>> exhaustive() {
		return exhaustive(ExhaustiveGenerator.MAXIMUM_SAMPLES_TO_GENERATE);
	}

	/**
	 * Create the exhaustive generator for an arbitrary. Depending on
	 * {@code maxNumberOfSamples} this can take a long time.
	 * This method must be overridden in all arbitraries that support exhaustive
	 * generation.
	 *
	 * @param maxNumberOfSamples The maximum number of samples considered.
	 *                           If during generation it becomes clear that this
	 *                           number will be exceeded generation stops.
	 * @return a new exhaustive generator or Optional.empty() if it cannot be created.
	 */
	@API(status = INTERNAL)
	default Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return Optional.empty();
	}

	@API(status = EXPERIMENTAL, since = "1.3.0")
	EdgeCases<T> edgeCases();

	/**
	 * Create optional stream of all possible values this arbitrary could generate.
	 * This is only possible if the arbitrary is available for exhaustive generation.
	 *
	 * @return optional stream of all possible values
	 */
	default Optional<Stream<T>> allValues() {
		return exhaustive().map(generator -> StreamSupport.stream(generator.spliterator(), false));
	}

	/**
	 * Iterate through each value this arbitrary can generate if - and only if -
	 * exhaustive generation is possible. This method can be used for example
	 * to make assertions about a set of values described by an arbitrary.
	 *
	 * @param action the consumer function to be invoked for each value
	 * @throws AssertionError if exhaustive generation is not possible
	 */
	@API(status = MAINTAINED, since = "1.1.2")
	default void forEachValue(Consumer<? super T> action) {
		if (!allValues().isPresent())
			throw new AssertionError("Cannot generate all values of " + this.toString());
		allValues().ifPresent(
			stream -> stream.forEach(action::accept));
	}

	/**
	 * Create a new arbitrary of the same type {@code T} that creates and shrinks the original arbitrary but only allows
	 * values that are accepted by the {@code filterPredicate}.
	 *
	 * @return a new arbitrary instance
	 * @throws JqwikException if filtering will fail to come up with a value after 10000 tries
	 */
	default Arbitrary<T> filter(Predicate<T> filterPredicate) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return Arbitrary.this.generator(genSize).filter(filterPredicate);
			}

			@Override
			public boolean isUnique() {
				return Arbitrary.this.isUnique();
			}


			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples)
									 .map(generator -> generator.filter(filterPredicate));
			}

			@Override
			public EdgeCases<T> edgeCases() {
				return Arbitrary.this.edgeCases().filter(filterPredicate);
			}
		};
	}

	/**
	 * Create a new arbitrary of type {@code U} that maps the values of the original arbitrary using the {@code mapper}
	 * function.
	 *
	 * @return a new arbitrary instance
	 */
	default <U> Arbitrary<U> map(Function<T, U> mapper) {
		return new Arbitrary<U>() {
			@Override
			public RandomGenerator<U> generator(int genSize) {
				return Arbitrary.this.generator(genSize).map(mapper);
			}

			@Override
			public boolean isUnique() {
				return Arbitrary.this.isUnique();
			}

			@Override
			public Optional<ExhaustiveGenerator<U>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples)
									 .map(generator -> generator.map(mapper));
			}

			@Override
			public EdgeCases<U> edgeCases() {
				return Arbitrary.this.edgeCases().map(mapper);
			}
		};
	}

	/**
	 * Create a new arbitrary of type {@code U} that uses the values of the existing arbitrary to create a new arbitrary
	 * using the {@code mapper} function.
	 *
	 * @return a new arbitrary instance
	 */
	default <U> Arbitrary<U> flatMap(Function<T, Arbitrary<U>> mapper) {
		return new Arbitrary<U>() {
			@Override
			public RandomGenerator<U> generator(int genSize) {
				return Arbitrary.this.generator(genSize).flatMap(mapper, genSize);
			}

			@Override
			public Optional<ExhaustiveGenerator<U>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples)
									 .flatMap(generator -> ArbitraryFacade.implementation
															   .flatMapExhaustiveGenerator(generator, mapper, maxNumberOfSamples));
			}

			@Override
			public EdgeCases<U> edgeCases() {
				return Arbitrary.this.edgeCases().flatMapArbitrary(mapper);
			}
		};
	}

	/**
	 * Create a new arbitrary of the same type but inject null values with a probability of {@code nullProbability}.
	 *
	 * @return a new arbitrary instance
	 */
	default Arbitrary<T> injectNull(double nullProbability) {
		if (nullProbability <= 0.0) {
			return this;
		}
		if (nullProbability >= 1.0) {
			return Arbitraries.constant(null);
		}
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return Arbitrary.this.generator(genSize).injectNull(nullProbability);
			}

			@Override
			public boolean isUnique() {
				return Arbitrary.this.isUnique();
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples).map(ExhaustiveGenerator::injectNull);
			}

			@Override
			public EdgeCases<T> edgeCases() {
				EdgeCases<T> nullSupplier = EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(null));
				return EdgeCases.concat(Arbitrary.this.edgeCases(), nullSupplier);
			}
		};
	}

	/**
	 * Create a new arbitrary of the same type {@code T} that creates and shrinks the original arbitrary but will
	 * never generate the same value twice.
	 *
	 * @return a new arbitrary instance
	 * @throws JqwikException if filtering will fail to come up with a value after 10000 tries
	 */
	default Arbitrary<T> unique() {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return Arbitrary.this.generator(genSize).unique();
			}

			@Override
			public boolean isUnique() {
				return true;
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples).map(ExhaustiveGenerator::unique);
			}

			@Override
			public EdgeCases<T> edgeCases() {
				return Arbitrary.this.edgeCases();
			}

		};
	}

	/**
	 * Fix the genSize of an arbitrary so that it can no longer be influenced from outside
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.2.0")
	default Arbitrary<T> fixGenSize(int genSize) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int ignoredGenSize) {
				return Arbitrary.this.generator(genSize);
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples);
			}

			@Override
			public EdgeCases<T> edgeCases() {
				return Arbitrary.this.edgeCases();
			}
		};
	}

	/**
	 * Create a new arbitrary of type {@code List<T>} using the existing arbitrary for generating the elements of the list.
	 */
	default StreamableArbitrary<T, List<T>> list() {
		return ArbitraryFacade.implementation.list(this);
	}

	/**
	 * Create a new arbitrary of type {@code Set<T>} using the existing arbitrary for generating the elements of the set.
	 *
	 * @return a new arbitrary instance
	 */
	default SetArbitrary<T> set() {
		return ArbitraryFacade.implementation.set(this);
	}

	/**
	 * Create a new arbitrary of type {@code Stream<T>} using the existing arbitrary for generating the elements of the
	 * stream.
	 *
	 * @return a new arbitrary instance
	 */
	default StreamableArbitrary<T, Stream<T>> stream() {
		return ArbitraryFacade.implementation.stream(this);
	}

	/**
	 * Create a new arbitrary of type {@code Iterable<T>} using the existing arbitrary for generating the elements of the
	 * stream.
	 *
	 * @return a new arbitrary instance
	 */
	default StreamableArbitrary<T, Iterator<T>> iterator() {
		return ArbitraryFacade.implementation.iterator(this);
	}

	/**
	 * Create a new arbitrary of type {@code T[]} using the existing arbitrary for generating the elements of the array.
	 *
	 * @param arrayClass The arrays class to create, e.g. {@code String[].class}. This is required due to limitations in Java's
	 *                   reflection capabilities.
	 * @return a new arbitrary instance
	 */
	default <A> StreamableArbitrary<T, A> array(Class<A> arrayClass) {
		return ArbitraryFacade.implementation.array(this, arrayClass);
	}

	/**
	 * Create a new arbitrary of type {@code Optional<T>} using the existing arbitrary for generating the elements of the
	 * stream.
	 *
	 * <p>
	 * The new arbitrary also generates {@code Optional.empty()} values with a probability of {@code 0.05} (i.e. 1 in 20).
	 * </p>
	 *
	 * @return a new arbitrary instance
	 */
	default Arbitrary<Optional<T>> optional() {
		return this.injectNull(0.05).map(Optional::ofNullable);
	}

	/**
	 * Create a new arbitrary of type {@code List<T>} by adding elements of type T until condition {@code until} is fulfilled.
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	default Arbitrary<List<T>> collect(Predicate<List<T>> until) {
		return new Arbitrary<List<T>>() {
			@Override
			public RandomGenerator<List<T>> generator(final int genSize) {
				return Arbitrary.this.generator(genSize).collect(until);
			}

			@Override
			public EdgeCases<List<T>> edgeCases() {
				return EdgeCases.none();
			}
		};
	}

	/**
	 * Generate a stream of sample values using this arbitrary.
	 * This can be useful for
	 * <ul>
	 * 		<li>Testing arbitraries</li>
	 * 		<li>Playing around with arbitraries in <em>jshell</em></li>
	 * 		<li>Using arbitraries independently from jqwik, e.g. to feed test data builders</li>
	 * </ul>
	 *
	 * <p>
	 * The underlying generator is created with size 1000.
	 * Outside a property a new instance of {@linkplain Random} will be created
	 * to feed the generator.
	 * <p>
	 *
	 * <p>
	 * Using this method within a property does not break reproducibility of results,
	 * i.e. rerunning it with same seed will also generate the same values.
	 * </p>
	 *
	 * @return a stream of newly generated values
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	default Stream<T> sampleStream() {
		return ArbitraryFacade.implementation.sampleStream(this);
	}

	/**
	 * Generate a single sample value using this arbitrary.
	 * This can be useful for
	 * <ul>
	 * 		<li>Testing arbitraries</li>
	 * 		<li>Playing around with arbitraries in <em>jshell</em></li>
	 * 		<li>Using arbitraries independently from jqwik, e.g. to feed test data builders</li>
	 * </ul>
	 * <p>
	 * Some additional things to be aware of:
	 * <ul>
	 *     <li>
	 *         If you feel the need to use this method for real generation, e.g. in a provider method
	 *         you are most probably doing it wrong. You might want to use {@linkplain Arbitrary#flatMap(Function)}.
	 *     </li>
	 *     <li>
	 *         The underlying generator is created with size 1000.
	 * 	       Outside a property a new instance of {@linkplain Random} will be created
	 * 	       to feed the generator.</li>
	 *     <li>
	 *         Using this method within a property does not break reproducibility of results,
	 * 	       i.e. rerunning it with same seed will also generate the same value.
	 *     </li>
	 * </ul>
	 *
	 * @return a newly generated value
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	default T sample() {
		return this.sampleStream()
				   .findFirst()
				   .orElseThrow(() -> new JqwikException("Cannot generate a value"));
	}

	/**
	 * Create a new arbitrary of type {@code Iterable<T>} that will
	 * inject duplicates of previously generated values with a probability of {@code duplicateProbability}.
	 *
	 * <p>
	 * Shrinking behavior for duplicate values
	 * -- if duplication is required for falsification -- is poor,
	 * i.e. those duplicate values cannot be shrunk to "smaller" duplicate values.
	 * </p>
	 *
	 * @param duplicateProbability The probability with which a previous value will be generated
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	default Arbitrary<T> injectDuplicates(double duplicateProbability) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return Arbitrary.this.generator(genSize).injectDuplicates(duplicateProbability);
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples);
			}

			@Override
			public EdgeCases<T> edgeCases() {
				return Arbitrary.this.edgeCases();
			}
		};
	}

	/**
	 * Create a new arbitrary of type {@code Tuple.Tuple1<T>} that will use the underlying
	 * arbitrary to create the tuple value;
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	default Arbitrary<Tuple.Tuple1<T>> tuple1() {
		return Arbitrary.this.map(Tuple::of);
	}

	/**
	 * Create a new arbitrary of type {@code Tuple.Tuple2<T, T>} that will use the underlying
	 * arbitrary to create the tuple values;
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	default Arbitrary<Tuple.Tuple2<T, T>> tuple2() {
		return Arbitrary.this.list().ofSize(2).map(l -> Tuple.of(l.get(0), l.get(1)));
	}

	/**
	 * Create a new arbitrary of type {@code Tuple.Tuple3<T, T, T>} that will use the underlying
	 * arbitrary to create the tuple values;
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	default Arbitrary<Tuple.Tuple3<T, T, T>> tuple3() {
		return Arbitrary.this.list().ofSize(3).map(l -> Tuple.of(l.get(0), l.get(1), l.get(2)));
	}

	/**
	 * Create a new arbitrary of type {@code Tuple.Tuple4<T, T, T, T>} that will use the underlying
	 * arbitrary to create the tuple values;
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.3.0")
	default Arbitrary<Tuple.Tuple4<T, T, T, T>> tuple4() {
		return Arbitrary.this.list().ofSize(4).map(l -> Tuple.of(l.get(0), l.get(1), l.get(2), l.get(3)));
	}

	/**
	 * Create a new arbitrary of type {@code T} that will use the underlying
	 * arbitrary to create the tuple values but will ignore any raised exception of
	 * type {@code exceptionType} during generation.
	 *
	 * @param exceptionType The exception type to ignore
	 * @return a new arbitrary instance
	 */
	@API(status = EXPERIMENTAL, since = "1.3.1")
	default Arbitrary<T> ignoreException(Class<? extends Throwable> exceptionType) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return Arbitrary.this.generator(genSize).ignoreException(exceptionType);
			}

			@Override
			public boolean isUnique() {
				return Arbitrary.this.isUnique();
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples)
									 .map(generator -> generator.ignoreException(exceptionType));
			}

			@Override
			public EdgeCases<T> edgeCases() {
				return Arbitrary.this.edgeCases().ignoreException(exceptionType);
			}
		};
	}

	/**
	 * Create a new arbitrary of type {@code T} that will use the underlying
	 * arbitrary to create the tuple values but will return unshrinkable values.
	 * This might be necessary if values are being mutated during a property run
	 * and the mutated state would make a shrunk value invalid.
	 *
	 * <p>
	 *     This is a hack to get around a weakness in jqwik's shrinking mechanism
	 * </p>
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = EXPERIMENTAL, since = "1.3.2")
	default Arbitrary<T> dontShrink() {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return Arbitrary.this.generator(genSize).dontShrink();
			}

			@Override
			public boolean isUnique() {
				return Arbitrary.this.isUnique();
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return Arbitrary.this.exhaustive(maxNumberOfSamples);
			}

			@Override
			public EdgeCases<T> edgeCases() {
				return Arbitrary.this.edgeCases().dontShrink();
			}
		};
	}

}
