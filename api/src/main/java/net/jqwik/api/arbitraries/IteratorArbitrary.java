package net.jqwik.api.arbitraries;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain Iterator}
 */
@API(status = EXPERIMENTAL, since = "1.3.2")
public interface IteratorArbitrary<T> extends StreamableArbitrary<T, Iterator<T>> {

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

}
