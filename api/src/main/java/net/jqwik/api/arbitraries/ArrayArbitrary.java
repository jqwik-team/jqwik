package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of Arrays
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface ArrayArbitrary<T, A> extends StreamableArbitrary<T, A> {

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

}
