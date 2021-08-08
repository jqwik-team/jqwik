package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

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
	 * @return new instance of arbitrary
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
	 * @return new instance of arbitrary
	 */
	DoubleArbitrary between(double min, boolean minIncluded, double max, boolean maxIncluded);

	/**
	 * Set the allowed lower {@code min} (included) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 * @return new instance of arbitrary
	 */
	DoubleArbitrary greaterOrEqual(double min);

	/**
	 * Set the allowed lower {@code min} (excluded) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.2.7")
	DoubleArbitrary greaterThan(double min);

	/**
	 * Set the allowed upper {@code max} (included) border of generated numbers.
	 *
	 * @param max The upper border of possible values
	 * @return new instance of arbitrary
	 */
	DoubleArbitrary lessOrEqual(double max);

	/**
	 * Set the allowed upper {@code max} (excluded) border of generated numbers.
	 *
	 * @param max The upper border of possible values
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.2.7")
	DoubleArbitrary lessThan(double max);

	/**
	 * Set the scale (maximum number of decimal places) to {@code scale}.
	 *
	 * @param scale number of decimal places
	 * @return new instance of arbitrary
	 */
	DoubleArbitrary ofScale(int scale);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 *
	 * @param target The value which is considered to be the most simple value for shrinking
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	DoubleArbitrary shrinkTowards(double target);

	/**
	 * Inject a special value into generated values and edge cases.
	 * This value can be outside the constraints of the arbitrary, 
	 * e.g. have more decimals than specified by {@linkplain #ofScale(int)}.
	 *
	 * @param special value
	 * @return new instance of arbitrary
	 */
	@API(status = EXPERIMENTAL, since = "1.5.4")
	DoubleArbitrary withSpecialValue(double special);

	/**
	 * Inject a selection of special values using {@linkplain #withSpecialValue(double)}:
	 * <ul>
	 *     <li>{@linkplain Double#NaN}</li>
	 *     <li>{@linkplain Double#MIN_VALUE}</li>
	 *     <li>{@linkplain Double#MIN_NORMAL}</li>
	 *     <li>{@linkplain Double#POSITIVE_INFINITY}</li>
	 *     <li>{@linkplain Double#NEGATIVE_INFINITY}</li>
	 * </ul>
	 * This value can be outside the constraints of the arbitrary,
	 * e.g. have more decimals than specified by {@linkplain #ofScale(int)}.
	 *
	 * @return new instance of arbitrary
	 */
	@API(status = EXPERIMENTAL, since = "1.5.4")
	DoubleArbitrary withStandardSpecialValues();
}
