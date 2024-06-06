package net.jqwik.api.arbitraries;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain List}
 */
@API(status = MAINTAINED, since = "1.3.2")
public interface ListArbitrary<T extends @Nullable Object> extends Arbitrary<List<T>>, StreamableArbitrary<T, List<T>> {

	/**
	 * Fix the size to {@code size}.
	 *
	 * @param size The size of the generated list
	 * @return new arbitrary instance
	 */
	@Override
	default ListArbitrary<T> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 *
	 * @param minSize The minimum size of the generated list
	 * @return new arbitrary instance
	 */
	ListArbitrary<T> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 *
	 * @param maxSize The maximum size of the generated list
	 * @return new arbitrary instance
	 */
	ListArbitrary<T> ofMaxSize(int maxSize);

	/**
	 * Set random distribution {@code distribution} of size of generated list.
	 * The distribution's center is the minimum size of the generated list.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.3")
	ListArbitrary<T> withSizeDistribution(RandomDistribution uniform);

	/**
	 * Map over each element of the generated list giving access to all elements of the list.
	 * The number and position of the mapped elements within the list will stay the same.
	 *
	 * @param <U> The target type of a list to generate
	 * @param mapper Mapper function to element type U
	 * @return arbitrary of a list of Us
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	<U> Arbitrary<List<U>> mapEach(BiFunction<List<T>, T, U> mapper);

	/**
	 * Flat-map over each element of the generated list giving access to all elements of the list.
	 * The number and position of the mapped elements within the list will stay the same.
	 *
	 * @param <U> The target type of a list to generate
	 * @param flatMapper Mapper function to arbitrary of element type U
	 * @return arbitrary of a list of Us
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	<U> Arbitrary<List<U>> flatMapEach(BiFunction<? super List<? extends T>, ? super T, ? extends Arbitrary<U>> flatMapper);

	/**
	 * Add the constraint that elements of the generated list must be unique,
	 * i.e. no two elements must return true when being compared using {@linkplain Object#equals(Object)}.
	 *
	 * <p>
	 *     The constraint can be combined with other {@linkplain #uniqueElements(Function)} constraints.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	ListArbitrary<T> uniqueElements();

	/**
	 * Add the constraint that elements of the generated list must be unique
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
	ListArbitrary<T> uniqueElements(Function<? super T, ?> by);
}
