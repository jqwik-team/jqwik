package net.jqwik.time.api.arbitraries;

import java.time.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of date values.
 * All generated dates use the Gregorian Calendar, even if they are before October 15, 1582.
 * By default, dates with years between 1900 and 2500 are generated.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public interface CalendarArbitrary extends Arbitrary<Calendar> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated calendar values.
	 */
	default CalendarArbitrary between(Calendar min, Calendar max) {
		if (min.after(max)) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated calendar values.
	 */
	CalendarArbitrary atTheEarliest(Calendar min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated calendar values.
	 */
	CalendarArbitrary atTheLatest(Calendar max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The years can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #between(Calendar, Calendar)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	CalendarArbitrary yearBetween(Year min, Year max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The {@code int} values can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #between(Calendar, Calendar)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	default CalendarArbitrary yearBetween(int min, int max) {
		return yearBetween(Year.of(min), Year.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	CalendarArbitrary monthBetween(Month min, Month max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 * The {@code int} values can be between 1 and 12.
	 */
	default CalendarArbitrary monthBetween(int min, int max) {
		return monthBetween(Month.of(min), Month.of(max));
	}

	/**
	 * Set an array of allowed {@code months}.
	 */
	CalendarArbitrary onlyMonths(Month... months);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day of month values.
	 * The {@code int} values can be between 1 and 31.
	 */
	CalendarArbitrary dayOfMonthBetween(int min, int max);

	/**
	 * Set an array of allowed {@code daysOfWeek}.
	 */
	CalendarArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek);

}
