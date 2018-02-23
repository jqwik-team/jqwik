package net.jqwik.api.arbitraries;

/**
 * Fluent interface to configure the generation of Integer and int values.
 */
public interface IntegerArbitrary extends NullableArbitrary<Integer> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default IntegerArbitrary between(int min, int max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	IntegerArbitrary greaterOrEqual(int min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	IntegerArbitrary lessOrEqual(int max);
}
