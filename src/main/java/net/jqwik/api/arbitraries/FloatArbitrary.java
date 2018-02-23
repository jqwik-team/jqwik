package net.jqwik.api.arbitraries;

/**
 * Fluent interface to configure the generation of Float and float values.
 */
public interface FloatArbitrary extends NullableArbitrary<Float> {

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
}
