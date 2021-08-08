package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

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
	@API(status = MAINTAINED, since = "1.4.0")
	FloatArbitrary shrinkTowards(float target);

	/**
	 * Inject a special value into generated values and edge cases.
	 * This value can be outside the constraints of the arbitrary,
	 * e.g. have more decimals than specified by {@linkplain #ofScale(int)}.
	 *
	 * @param special value
	 * @return new instance of arbitrary
	 */
	@API(status = EXPERIMENTAL, since = "1.5.4")
	FloatArbitrary withSpecialValue(float special);

	/**
	 * Inject a selection of special values using {@linkplain #withSpecialValue(float)}:
	 * <ul>
	 *     <li>{@linkplain Float#NaN}</li>
	 *     <li>{@linkplain Float#MIN_VALUE}</li>
	 *     <li>{@linkplain Float#MIN_NORMAL}</li>
	 *     <li>{@linkplain Float#POSITIVE_INFINITY}</li>
	 *     <li>{@linkplain Float#NEGATIVE_INFINITY}</li>
	 * </ul>
	 * This value can be outside the constraints of the arbitrary,
	 * e.g. have more decimals than specified by {@linkplain #ofScale(int)}.
	 *
	 * @return new instance of arbitrary
	 */
	@API(status = EXPERIMENTAL, since = "1.5.4")
	FloatArbitrary withStandardSpecialValues();

}
