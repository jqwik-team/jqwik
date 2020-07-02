package net.jqwik.api.arbitraries;

import java.util.stream.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain Stream}
 */
@API(status = EXPERIMENTAL, since = "1.3.2")
public interface StreamArbitrary<T> extends StreamableArbitrary<T, Stream<T>> {

	/**
	 * Fix the size to {@code size}.
	 */
	StreamArbitrary<T> ofSize(int size);

	/**
	 * Set lower size boundary {@code minSize} (included).
	 */
	StreamArbitrary<T> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 */
	StreamArbitrary<T> ofMaxSize(int maxSize);

}
