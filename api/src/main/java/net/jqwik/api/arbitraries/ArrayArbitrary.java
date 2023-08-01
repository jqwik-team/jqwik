package net.jqwik.api.arbitraries;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of Arrays
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface ArrayArbitrary<@Nullable T, A> extends StreamableArbitrary<T, A> {

	/**
	 * Fix the size to {@code size}.
	 *
	 * @param size The size of the generated set
	 * @return new arbitrary instance
	 */
	@Override
	default ArrayArbitrary<T, A> ofSize(int size) {
		return this.ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 *
	 * @param minSize The minimum size of the generated set
	 * @return new arbitrary instance
	 */
	ArrayArbitrary<T, A> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 *
	 * @param maxSize The maximum size of the generated set
	 * @return new arbitrary instance
	 */
	ArrayArbitrary<T, A> ofMaxSize(int maxSize);

	/**
	 * Set random distribution {@code distribution} of size of generated array.
	 * The distribution's center is the minimum size of the generated array.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.3")
	ArrayArbitrary<T, A> withSizeDistribution(RandomDistribution uniform);

	/**
	 * Add the constraint that elements of the generated array must be unique,
	 * i.e. no two elements must return true when being compared using {@linkplain Object#equals(Object)}.
	 *
	 * <p>
	 * The constraint can be combined with other {@linkplain #uniqueElements(Function)} constraints.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	ArrayArbitrary<T, A> uniqueElements();

	/**
	 * Add the constraint that elements of the generated array must be unique
	 * relating to an element's "feature" being extracted using the
	 * {@code by} function.
	 * The extracted features are being compared using {@linkplain Object#equals(Object)}.
	 *
	 * <p>
	 * The constraint can be combined with other {@linkplain #uniqueElements(Function)} constraints.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	ArrayArbitrary<T, A> uniqueElements(Function<T, Object> by);

}
