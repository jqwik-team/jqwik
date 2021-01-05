package net.jqwik.time.api;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Year values.
 * By default, years between 1900 and 2500 are generated.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public interface YearArbitrary extends Arbitrary<Year> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 */
	YearArbitrary between(Year min, Year max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The {@code int} values can be between {@code Year.MIN_VALUE} and {@code Year.MAX_VALUE}.
	 */
	default YearArbitrary between(int min, int max) {
		return between(Year.of(min), Year.of(max));
	}

}
