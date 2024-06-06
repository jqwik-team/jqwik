package net.jqwik.api.arbitraries;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain Iterator}
 */
@API(status = MAINTAINED, since = "1.3.2")
public interface IteratorArbitrary<T extends @Nullable Object> extends Arbitrary<Iterator<T>>, StreamableArbitrary<T, Iterator<T>> {

	/**
	 * Fix the size to {@code size}.
	 *
	 * @param size The size of the generated iterator
	 * @return new arbitrary instance
	 */
	@Override
	default IteratorArbitrary<T> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 *
	 * @param minSize The minimum size of the generated iterator
	 * @return new arbitrary instance
	 */
	IteratorArbitrary<T> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 *
	 * @param maxSize The maximum size of the generated iterator
	 * @return new arbitrary instance
	 */
	IteratorArbitrary<T> ofMaxSize(int maxSize);

	/**
	 * Set random distribution {@code distribution} of size of generated iterator.
	 * The distribution's center is the minimum size of the generated iterator.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.3")
	IteratorArbitrary<T> withSizeDistribution(RandomDistribution uniform);

	/**
	 * Add the constraint that elements of the generated iterator must be unique,
	 * i.e. no two elements must return true when being compared using {@linkplain Object#equals(Object)}.
	 *
	 * <p>
	 *     The constraint can be combined with other {@linkplain #uniqueElements(Function)} constraints.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	IteratorArbitrary<T> uniqueElements();

	/**
	 * Add the constraint that elements of the generated iterator must be unique
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
	IteratorArbitrary<T> uniqueElements(Function<? super T, ?> by);

}
