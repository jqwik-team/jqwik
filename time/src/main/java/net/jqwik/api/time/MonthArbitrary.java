package net.jqwik.api.time;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of month values.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public interface MonthArbitrary extends Arbitrary<Month> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	default MonthArbitrary between(Month min, Month max) {
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	default MonthArbitrary between(int min, int max) {
		return between(Month.of(min), Month.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated month values.
	 */
	MonthArbitrary atTheEarliest(Month min);

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated month values.
	 */
	default MonthArbitrary atTheEarliest(int min) {
		return atTheEarliest(Month.of(min));
	}

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated month values.
	 */
	MonthArbitrary atTheLatest(Month max);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated month values.
	 */
	default MonthArbitrary atTheLatest(int max) {
		return atTheLatest(Month.of(max));
	}

	/**
	 * Set an array of allowed {@code months}.
	 */
	MonthArbitrary only(Month... months);

}
