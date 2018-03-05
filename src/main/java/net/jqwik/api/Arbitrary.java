package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.properties.arbitraries.*;

/**
 * The main interface for representing objects that can be generated and shrunk.
 *
 * @param <T>
 *            The type of generated objects. Primitive objects (e.g. int, boolean etc.) are represented by their boxed
 *            type (e.g. Integer, Boolean).
 */
public interface Arbitrary<T> {
	RandomGenerator<T> generator(int tries);

	/**
	 * Create a new arbitrary of the same type {@code T} that creates and shrinks the original arbitrary but only allows
	 * values that are accepted by the {@code filterPredicate}.
	 *
	 */
	default Arbitrary<T> filter(Predicate<T> filterPredicate) {
		return tries -> new FilteredGenerator<T>(Arbitrary.this.generator(tries), filterPredicate);
	}

	/**
	 * Create a new arbitrary of type {@code U} that maps the values of the original arbitrary using the {@code mapper}
	 * function.
	 */
	default <U> Arbitrary<U> map(Function<T, U> mapper) {
		return tries -> Arbitrary.this.generator(tries).map(mapper);
	}

	/**
	 * Create a new arbitrary of type {@code U} that uses the values of the existing arbitrary to create a new arbitrary
	 * using the {@code mapper} function.
	 */
	default <U> Arbitrary<U> flatMap(Function<T, Arbitrary<U>> mapper) {
		return tries -> Arbitrary.this.generator(tries).flatMap(mapper, tries);
	}

	/**
	 * Create a new arbitrary of the same type but inject null values with a probability of {@code nullProbability}.
	 */
	default Arbitrary<T> injectNull(double nullProbability) {
		if (nullProbability <= 0.0) {
			return this;
		}
		return tries -> Arbitrary.this.generator(tries).injectNull(nullProbability);
	}

	/**
	 * Create a new arbitrary of the same type but inject values in{@code samples} first before continuing with standard
	 * value generation.
	 */
	@SuppressWarnings("unchecked")
	default Arbitrary<T> withSamples(T... samples) {
		return tries -> Arbitrary.this.generator(tries).withSamples(samples);
	}

	/**
	 * Create a new arbitrary of type {@code List<T>} using the existing arbitrary for generating the elements of the list.
	 */
	default SizableArbitrary<List<T>> list() {
		return new ListArbitrary<T>(this);
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
		return new ArrayArbitrary<A, T>(arrayClass, this);
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

	static int defaultMaxCollectionSizeFromTries(int tries) {
		return (int) Math.max(Math.round(Math.sqrt(tries)), 10);
	}
}
