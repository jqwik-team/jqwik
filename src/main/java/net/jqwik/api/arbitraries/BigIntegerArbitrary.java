package net.jqwik.api.arbitraries;

import java.math.*;

/**
 * Fluent interface to configure the generation of BigInteger values.
 */
public interface BigIntegerArbitrary extends NullableArbitrary<BigInteger> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default BigIntegerArbitrary between(BigInteger min, BigInteger max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	BigIntegerArbitrary greaterOrEqual(BigInteger min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	BigIntegerArbitrary lessOrEqual(BigInteger max);
}
