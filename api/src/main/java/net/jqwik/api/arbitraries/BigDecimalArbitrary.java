package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

import java.math.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of BigDecimal values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface BigDecimalArbitrary extends Arbitrary<BigDecimal> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default BigDecimalArbitrary between(BigDecimal min, BigDecimal max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	BigDecimalArbitrary greaterOrEqual(BigDecimal min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	BigDecimalArbitrary lessOrEqual(BigDecimal max);

	/**
	 * Set the scale (maximum number of decimal places) to {@code scale}.
	 */
	BigDecimalArbitrary ofScale(int scale);
}
