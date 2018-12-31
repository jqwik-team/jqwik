package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Integer and int values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface IntegerArbitrary extends Arbitrary<Integer> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default IntegerArbitrary between(int min, int max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	IntegerArbitrary greaterOrEqual(int min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	IntegerArbitrary lessOrEqual(int max);
}
