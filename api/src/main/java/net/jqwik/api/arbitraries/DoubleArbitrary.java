package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Double and double values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface DoubleArbitrary extends Arbitrary<Double> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default DoubleArbitrary between(double min, double max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	DoubleArbitrary greaterOrEqual(double min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	DoubleArbitrary lessOrEqual(double max);

	/**
	 * Set the scale (maximum number of decimal places) to {@code scale}.
	 */
	DoubleArbitrary ofScale(int scale);

}
