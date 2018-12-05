package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

/**
 * Fluent interface to configure the generation of Byte and byte values.
 */
public interface ByteArbitrary extends Arbitrary<Byte> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (includedI bounder of generated numbers.
	 */
	default ByteArbitrary between(byte min, byte max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	ByteArbitrary greaterOrEqual(byte min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	ByteArbitrary lessOrEqual(byte max);
}
