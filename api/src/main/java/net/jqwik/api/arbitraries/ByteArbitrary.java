package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Byte and byte values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface ByteArbitrary extends Arbitrary<Byte> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounds of generated numbers.
	 */
	default ByteArbitrary between(byte min, byte max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bound of generated numbers.
	 */
	ByteArbitrary greaterOrEqual(byte min);

	/**
	 * Set the allowed upper {@code max} (included) bound of generated numbers.
	 */
	ByteArbitrary lessOrEqual(byte max);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 */
	@API(status = EXPERIMENTAL, since = "1.1.4")
	Arbitrary<Byte> shrinkTowards(int target);
}
