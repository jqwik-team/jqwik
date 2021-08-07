package net.jqwik.time.api.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of instant values.
 * All generated instants use the Gregorian Calendar, even if they are before October 15, 1582.
 * By default, instants with years between 1900 and 2500 are generated.
 * Max possible year is 999_999_999.
 */
@API(status = EXPERIMENTAL, since = "1.5.4")
public interface InstantArbitrary extends Arbitrary<Instant> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated instant values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	default InstantArbitrary between(Instant min, Instant max) {
		if (min.isAfter(max)) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated instant values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	InstantArbitrary atTheEarliest(Instant min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated instant values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	InstantArbitrary atTheLatest(Instant max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated local date values.
	 */
	InstantArbitrary dateBetween(LocalDate min, LocalDate max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The years can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #dateBetween(LocalDate, LocalDate)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	InstantArbitrary yearBetween(Year min, Year max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The {@code int} values can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #dateBetween(LocalDate, LocalDate)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	default InstantArbitrary yearBetween(int min, int max) {
		return yearBetween(Year.of(min), Year.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	InstantArbitrary monthBetween(Month min, Month max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 * The {@code int} values can be between 1 and 12.
	 */
	default InstantArbitrary monthBetween(int min, int max) {
		return monthBetween(Month.of(min), Month.of(max));
	}

	/**
	 * Set an array of allowed {@code months}.
	 */
	InstantArbitrary onlyMonths(Month... months);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day of month values.
	 * The {@code int} values can be between 1 and 31.
	 */
	InstantArbitrary dayOfMonthBetween(int min, int max);

	/**
	 * Set an array of allowed {@code daysOfWeek}.
	 */
	InstantArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated local time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	InstantArbitrary timeBetween(LocalTime min, LocalTime max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated hour values.
	 * The hours can be between {@code 0} and {@code 23}.
	 */
	InstantArbitrary hourBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated minute values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	InstantArbitrary minuteBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated second values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	InstantArbitrary secondBetween(int min, int max);

	/**
	 * Constrain the precision of generated values.
	 * Default value: Seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	InstantArbitrary ofPrecision(ChronoUnit ofPrecision);

}
