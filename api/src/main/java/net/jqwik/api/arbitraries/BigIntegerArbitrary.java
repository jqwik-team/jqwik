package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

import java.math.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of BigInteger values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface BigIntegerArbitrary extends Arbitrary<BigInteger> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default BigIntegerArbitrary between(BigInteger min, BigInteger max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	BigIntegerArbitrary greaterOrEqual(BigInteger min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	BigIntegerArbitrary lessOrEqual(BigInteger max);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 */
	@API(status = EXPERIMENTAL, since = "1.1.4")
	Arbitrary<BigInteger> shrinkTowards(BigInteger target);
}
