package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

/**
 * Fluent interface to configure the generation of Long and long values.
 */
public interface LongArbitrary extends Arbitrary<Long> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default LongArbitrary between(long min, long max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	LongArbitrary greaterOrEqual(long min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	LongArbitrary lessOrEqual(long max);
}
