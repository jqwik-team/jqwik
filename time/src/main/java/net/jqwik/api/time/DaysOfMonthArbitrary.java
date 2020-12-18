package net.jqwik.api.time;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of day of month values.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public interface DaysOfMonthArbitrary extends Arbitrary<Integer> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day of month values.
	 */
	default DaysOfMonthArbitrary between(int min, int max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated day of month values.
	 */
	DaysOfMonthArbitrary greaterOrEqual(int min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated day of month values.
	 */
	DaysOfMonthArbitrary lessOrEqual(int max);

}
