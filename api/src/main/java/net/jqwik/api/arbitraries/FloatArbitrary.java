package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Float and float values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface FloatArbitrary extends Arbitrary<Float> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default FloatArbitrary between(float min, float max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	FloatArbitrary greaterOrEqual(float min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	FloatArbitrary lessOrEqual(float max);

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
