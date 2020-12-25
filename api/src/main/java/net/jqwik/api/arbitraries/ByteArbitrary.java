package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Byte and byte values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface ByteArbitrary extends NumericalArbitrary<Byte, ByteArbitrary> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounds of generated numbers.
	 *
	 * @param min min value (included)
	 * @param max max value (included)
	 * @return new instance of arbitrary
	 */
	default ByteArbitrary between(byte min, byte max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bound of generated numbers.
	 *
	 * @param min min value (included)
	 * @return new instance of arbitrary
	 */
	ByteArbitrary greaterOrEqual(byte min);

	/**
	 * Set the allowed upper {@code max} (included) bound of generated numbers.
	 *
	 * @param max max value (included)
	 * @return new instance of arbitrary
	 */
	ByteArbitrary lessOrEqual(byte max);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 *
	 * @param target target shrinking value
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	ByteArbitrary shrinkTowards(int target);
}
