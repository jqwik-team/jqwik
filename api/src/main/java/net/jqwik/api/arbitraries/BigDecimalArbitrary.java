package net.jqwik.api.arbitraries;

import java.math.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of BigDecimal values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface BigDecimalArbitrary extends NumericalArbitrary<BigDecimal, BigDecimalArbitrary> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 * @param max The upper border of possible values
	 */
	default BigDecimalArbitrary between(BigDecimal min, BigDecimal max) {
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
	@API(status = MAINTAINED, since = "1.2.7")
	BigDecimalArbitrary between(BigDecimal min, boolean minIncluded, BigDecimal max, boolean maxIncluded);

	/**
	 * Set the allowed lower {@code min} (included) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 */
	BigDecimalArbitrary greaterOrEqual(BigDecimal min);

	/**
	 * Set the allowed lower {@code min} (excluded) border of generated numbers.
	 *
	 * @param min The lower border of possible values
	 */
	@API(status = MAINTAINED, since = "1.2.7")
	BigDecimalArbitrary greaterThan(BigDecimal min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 *
	 * @param max The upper border of possible values
	 */
	BigDecimalArbitrary lessOrEqual(BigDecimal max);

	/**
	 * Set the allowed upper {@code max} (excluded) border of generated numbers.
	 *
	 * @param max The upper border of possible values
	 */
	@API(status = MAINTAINED, since = "1.2.7")
	BigDecimalArbitrary lessThan(BigDecimal max);

	/**
	 * Set the scale (maximum number of decimal places) to {@code scale}.
	 */
	BigDecimalArbitrary ofScale(int scale);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 *
	 * @param target The value which is considered to be the most simple value for shrinking
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	BigDecimalArbitrary shrinkTowards(BigDecimal target);
}
