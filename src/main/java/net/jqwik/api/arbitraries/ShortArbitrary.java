package net.jqwik.api.arbitraries;

/**
 * Fluent interface to configure the generation of Short and short values.
 */
public interface ShortArbitrary extends NullableArbitrary<Short> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default ShortArbitrary between(short min, short max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	ShortArbitrary greaterOrEqual(short min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	ShortArbitrary lessOrEqual(short max);
}
