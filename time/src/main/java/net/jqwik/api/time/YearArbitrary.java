package net.jqwik.api.time;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Year values.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public interface YearArbitrary extends Arbitrary<Year> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 */
	default YearArbitrary between(Year min, Year max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 */
	default YearArbitrary between(int min, int max) {
		return between(Year.of(min), Year.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated year values.
	 */
	YearArbitrary greaterOrEqual(Year min);

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated year values.
	 */
	default YearArbitrary greaterOrEqual(int min) {
		return greaterOrEqual(Year.of(min));
	}

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated year values.
	 */
	YearArbitrary lessOrEqual(Year max);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated year values.
	 */
	default YearArbitrary lessOrEqual(int max) {
		return lessOrEqual(Year.of(max));
	}

}
