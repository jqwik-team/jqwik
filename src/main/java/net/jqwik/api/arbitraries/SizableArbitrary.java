package net.jqwik.api.arbitraries;

/**
 * Fluent interface to configure arbitraries that have size constraints for generated values, e.g. collections and arrays.
 */
public interface SizableArbitrary<U> extends NullableArbitrary<U> {

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
