package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Float and float values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface FloatArbitrary extends NumericalArbitrary<Float, FloatArbitrary> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 * @param max The upper border of possible values
	 */
	default FloatArbitrary between(float min, float max) {
		return between(min, true, max, true);
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) border of generated numbers.
	 * Specify if borders should be included in allowed values or not.
	 *
	 * @param min         The lower border of possible values
	 * @param minIncluded Should the lower border be included
	 * @param max         The upper border of possible values
	 * @param maxIncluded Should the upper border be included
	 */
	FloatArbitrary between(float min, boolean minIncluded, float max, boolean maxIncluded);

	/**
	 * Set the allowed lower {@code min} (included) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 */
	FloatArbitrary greaterOrEqual(float min);

	/**
	 * Set the allowed lower {@code min} (excluded) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 */
	@API(status = MAINTAINED, since = "1.2.7")
	FloatArbitrary greaterThan(float min);

	/**
	 * Set the allowed upper {@code max} (included) border of generated numbers.
	 *
	 * @param max The upper border of possible values
	 */
	FloatArbitrary lessOrEqual(float max);

	/**
	 * Set the allowed upper {@code max} (excluded) border of generated numbers.
	 *
	 * @param max The upper border of possible values
	 */
	@API(status = MAINTAINED, since = "1.2.7")
	FloatArbitrary lessThan(float max);

	/**
	 * Set the scale (maximum number of decimal places) to {@code scale}.
	 */
	FloatArbitrary ofScale(int scale);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 */
	@API(status = EXPERIMENTAL, since = "1.1.5")
	FloatArbitrary shrinkTowards(float target);
}
