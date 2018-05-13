package net.jqwik.api;

import net.jqwik.api.arbitraries.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * The main interface for representing objects that can be generated and shrunk.
 *
 * @param <T>
 *            The type of generated objects. Primitive objects (e.g. int, boolean etc.) are represented by their boxed
 *            type (e.g. Integer, Boolean).
 */
public interface Arbitrary<T> {

	/**
	 * Create the random generator for an arbitrary
	 *
	 * @param genSize a very unspecific configuration parameter that can be used
	 *                to influence the configuration and behaviour of a random generator
	 *                if and only if the generator wants to be influenced.
	 *                Many generators are independent of genSize.
	 *
	 *                The default value of {@code genSize} is the number of tries configured
	 *                for a property. Use {@linkplain Arbitrary#fixGenSize(int)} to fix
	 *                the parameter for a given arbitrary.
	 *
	 * @return a new random generator instance
	 */
	RandomGenerator<T> generator(int genSize);

	/**
	 * Create a new arbitrary of the same type {@code T} that creates and shrinks the original arbitrary but only allows
	 * values that are accepted by the {@code filterPredicate}.
	 *
	 */
	default Arbitrary<T> filter(Predicate<T> filterPredicate) {
		return genSize -> new FilteredGenerator<>(Arbitrary.this.generator(genSize), filterPredicate);
	}

	/**
	 * Create a new arbitrary of type {@code U} that maps the values of the original arbitrary using the {@code mapper}
	 * function.
	 */
	default <U> Arbitrary<U> map(Function<T, U> mapper) {
		return genSize -> Arbitrary.this.generator(genSize).map(mapper);
	}

	/**
	 * Create a new arbitrary of type {@code U} that uses the values of the existing arbitrary to create a new arbitrary
	 * using the {@code mapper} function.
	 */
	default <U> Arbitrary<U> flatMap(Function<T, Arbitrary<U>> mapper) {
		return genSize -> Arbitrary.this.generator(genSize).flatMap(mapper, genSize);
	}

	/**
	 * Create a new arbitrary of the same type but inject null values with a probability of {@code nullProbability}.
	 */
	default Arbitrary<T> injectNull(double nullProbability) {
		if (nullProbability <= 0.0) {
			return this;
		}
		return genSize -> Arbitrary.this.generator(genSize).injectNull(nullProbability);
	}

	/**
	 * Create a new arbitrary of the same type but inject values in{@code samples} first before continuing with standard
	 * value generation.
	 */
	@SuppressWarnings("unchecked")
	default Arbitrary<T> withSamples(T... samples) {
		return genSize -> Arbitrary.this.generator(genSize).withSamples(samples);
	}

	/**
	 * Fix the genSize of an arbitrary so that it can no longer be influenced from outside
	 */
	default Arbitrary<T> fixGenSize(int genSize) {
		return ignoredGenSize -> Arbitrary.this.generator(genSize);
	}

	/**
	 * Create a new arbitrary of type {@code List<T>} using the existing arbitrary for generating the elements of the list.
	 */
	default SizableArbitrary<List<T>> list() {
		return new ListArbitrary<>(this);
	}

	/**
	 * Create a new arbitrary of type {@code Set<T>} using the existing arbitrary for generating the elements of the set.
	 */
	default SizableArbitrary<Set<T>> set() {
		return new SetArbitrary<>(this);
	}

	/**
	 * Create a new arbitrary of type {@code Stream<T>} using the existing arbitrary for generating the elements of the
	 * stream.
	 */
	default SizableArbitrary<Stream<T>> stream() {
		return new StreamArbitrary<>(this);
	}

	/**
	 * Create a new arbitrary of type {@code T[]} using the existing arbitrary for generating the elements of the array.
	 *
	 * @param arrayClass
	 *            The arrays class to create, e.g. {@code String[].class}. This is required due to limitations in Java's
	 *            reflection capabilities.
	 */
	default <A> SizableArbitrary<A> array(Class<A> arrayClass) {
		return new ArrayArbitrary<>(arrayClass, this);
	}

	/**
	 * Create a new arbitrary of type {@code Optional<T>} using the existing arbitrary for generating the elements of the
	 * stream.
	 *
	 * The new arbitrary also generates {@code Optional.empty()} values with a probability of {@code 0.05} (i.e. 1 in 20).
	 */
	default Arbitrary<Optional<T>> optional() {
		return this.injectNull(0.05).map(Optional::ofNullable);
	}

}
