package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of month and day values.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public interface MonthDayArbitrary extends Arbitrary<MonthDay> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month and day values.
	 */
	default MonthDayArbitrary between(MonthDay min, MonthDay max) {
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated month and day values.
	 */
	MonthDayArbitrary atTheEarliest(MonthDay min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated month and day values.
	 */
	MonthDayArbitrary atTheLatest(MonthDay max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	MonthDayArbitrary monthBetween(Month min, Month max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 * The {@code int} values can be between 1 and 12.
	 */
	default MonthDayArbitrary monthBetween(int min, int max) {
		return monthBetween(Month.of(min), Month.of(max));
	}

	/**
	 * Set an array of allowed {@code months}.
	 */
	MonthDayArbitrary onlyMonths(Month... months);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day of month values.
	 * The {@code int} values can be between 1 and 31.
	 */
	MonthDayArbitrary dayOfMonthBetween(int min, int max);

}
