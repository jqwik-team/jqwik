package net.jqwik.api.arbitraries;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain Set}
 */
@API(status = MAINTAINED, since = "1.3.2")
public interface SetArbitrary<T> extends Arbitrary<Set<T>>, StreamableArbitrary<T, Set<T>> {

	/**
	 * Fix the size to {@code size}.
	 *
	 * @param size The size of the generated set
	 * @return new arbitrary instance
	 */
	@Override
	default SetArbitrary<T> ofSize(int size) {
		return this.ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 *
	 * @param minSize The minimum size of the generated set
	 * @return new arbitrary instance
	 */
	SetArbitrary<T> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 *
	 * @param maxSize The maximum size of the generated set
	 * @return new arbitrary instance
	 */
	SetArbitrary<T> ofMaxSize(int maxSize);

	/**
	 * Set random distribution {@code distribution} of size of generated set.
	 * The distribution's center is the minimum size of the generated set.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.3")
	SetArbitrary<T> withSizeDistribution(RandomDistribution uniform);

	/**
	 * Map over each element of the generated set giving access to all elements of the set.
	 * The number of the mapped elements within the set will stay the same.
	 *
	 * @param <U> The target type of a set to generate
	 * @param mapper Mapper function to element type U
	 * @return arbitrary of a set of Us
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	<U> Arbitrary<Set<U>> mapEach(BiFunction<Set<T>, T, U> mapper);

	/**
	 * Flat-map over each element of the generated set giving access to all elements of the set.
	 * The number of the mapped elements within the set will stay the same.
	 *
	 * @param <U> The target type of a set to generate
	 * @param flatMapper Mapper function to arbitrary of element type U
	 * @return arbitrary of a set of Us
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	<U> Arbitrary<Set<U>> flatMapEach(BiFunction<Set<T>, T, Arbitrary<U>> flatMapper);

	/**
	 * Do not use. Sets have unique elements anyway.
	 * It only exists for purposes of symmetry.
	 *
	 * @return same instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.7.3")
	SetArbitrary<T> uniqueElements();

	/**
	 * Add the constraint that elements of the generated set must be unique
	 * relating to an element's "feature" being extracted using the
	 * {@code by} function.
	 * The extracted features are being compared using {@linkplain Object#equals(Object)}.
	 *
	 * <p>
	 *     The constraint can be combined with other {@linkplain #uniqueElements(Function)} constraints.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	SetArbitrary<T> uniqueElements(Function<T, Object> by);

}
