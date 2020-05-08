package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Short and short values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface ShortArbitrary extends NumericalArbitrary<Short, ShortArbitrary> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated numbers.
	 */
	default ShortArbitrary between(short min, short max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated numbers.
	 */
	ShortArbitrary greaterOrEqual(short min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated numbers.
	 */
	ShortArbitrary lessOrEqual(short max);

	/**
	 * Set shrinking target to {@code target} which must be between the allowed bounds.
	 */
	@API(status = EXPERIMENTAL, since = "1.1.4")
	Arbitrary<Short> shrinkTowards(short target);
}
