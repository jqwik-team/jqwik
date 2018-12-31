package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that have size constraints for generated values, e.g. collections and arrays.
 */
@API(status = MAINTAINED, since = "1.0")
public interface SizableArbitrary<U> extends Arbitrary<U> {

	/**
	 * Fix the size to {@code size}.
	 */
	default SizableArbitrary<U> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 */
	SizableArbitrary<U> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 */
	SizableArbitrary<U> ofMaxSize(int maxSize);
}
