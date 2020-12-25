package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

import java.math.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of BigInteger values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface BigIntegerArbitrary extends NumericalArbitrary<BigInteger, BigIntegerArbitrary> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 *
	 * @param min min value (included)
	 * @param max max value (included)
	 * @return new instance of arbitrary
	 */
	default BigIntegerArbitrary between(BigInteger min, BigInteger max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 *
	 * @param min min value (included)
	 * @return new instance of arbitrary
	 */
	BigIntegerArbitrary greaterOrEqual(BigInteger min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 *
	 * @param max max value (included)
	 * @return new instance of arbitrary
	 */
	BigIntegerArbitrary lessOrEqual(BigInteger max);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 *
	 * @param target shrinking target value
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	BigIntegerArbitrary shrinkTowards(BigInteger target);
}
