package net.jqwik.api.arbitraries;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain Map}
 */
@API(status = MAINTAINED, since = "1.3.2")
public interface MapArbitrary<K, V> extends SizableArbitrary<Map<K, V>> {

	/**
	 * Fix the size to {@code size}.
	 *
	 * @param size The size of the generated map
	 * @return new arbitrary instance
	 */
	@Override
	default MapArbitrary<K, V> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 *
	 * @param minSize The minimum size of the generated map
	 * @return new arbitrary instance
	 */
	MapArbitrary<K, V> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 *
	 * @param maxSize The maximum size of the generated map
	 * @return new arbitrary instance
	 */
	MapArbitrary<K, V> ofMaxSize(int maxSize);

}
