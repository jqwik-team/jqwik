package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Double and double values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface DoubleArbitrary extends NumericalArbitrary<Double, DoubleArbitrary> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 * @param max The upper border of possible values
	 */
	default DoubleArbitrary between(double min, double max) {
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
	DoubleArbitrary between(double min, boolean minIncluded, double max, boolean maxIncluded);

	/**
	 * Set the allowed lower {@code min} (included) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 */
	DoubleArbitrary greaterOrEqual(double min);

	/**
	 * Set the allowed lower {@code min} (excluded) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 */
	@API(status = MAINTAINED, since = "1.2.7")
	DoubleArbitrary greaterThan(double min);

	/**
	 * Set the allowed upper {@code max} (included) border of generated numbers.
	 *
	 * @param max The upper border of possible values
	 */
	DoubleArbitrary lessOrEqual(double max);

	/**
	 * Set the allowed upper {@code max} (excluded) border of generated numbers.
	 *
	 * @param max The upper border of possible values
	 */
	@API(status = MAINTAINED, since = "1.2.7")
	DoubleArbitrary lessThan(double max);

	/**
	 * Set the scale (maximum number of decimal places) to {@code scale}.
	 */
	DoubleArbitrary ofScale(int scale);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 *
	 * @param target The value which is considered to be the most simple value for shrinking
	 */
	@API(status = EXPERIMENTAL, since = "1.1.5")
	DoubleArbitrary shrinkTowards(double target);
}
