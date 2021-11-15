package net.jqwik.time.api.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of zoned date time values.
 * All generated zoned date times use the Gregorian Calendar, even if they are before October 15, 1582.
 * By default, zoned date times with years between 1900 and 2500 are generated.
 */
@API(status = EXPERIMENTAL, since = "1.6.1")
public interface ZonedDateTimeArbitrary extends Arbitrary<ZonedDateTime> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated zoned date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	default ZonedDateTimeArbitrary between(LocalDateTime min, LocalDateTime max) {
		if (min.isAfter(max)) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated zoned date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	ZonedDateTimeArbitrary atTheEarliest(LocalDateTime min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated zoned date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	ZonedDateTimeArbitrary atTheLatest(LocalDateTime max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated zoned date values.
	 */
	ZonedDateTimeArbitrary dateBetween(LocalDate min, LocalDate max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The years can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #dateBetween(LocalDate, LocalDate)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	ZonedDateTimeArbitrary yearBetween(Year min, Year max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The {@code int} values can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #dateBetween(LocalDate, LocalDate)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	default ZonedDateTimeArbitrary yearBetween(int min, int max) {
		return yearBetween(Year.of(min), Year.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	ZonedDateTimeArbitrary monthBetween(Month min, Month max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 * The {@code int} values can be between 1 and 12.
	 */
	default ZonedDateTimeArbitrary monthBetween(int min, int max) {
		return monthBetween(Month.of(min), Month.of(max));
	}

	/**
	 * Set an array of allowed {@code months}.
	 */
	ZonedDateTimeArbitrary onlyMonths(Month... months);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day of month values.
	 * The {@code int} values can be between 1 and 31.
	 */
	ZonedDateTimeArbitrary dayOfMonthBetween(int min, int max);

	/**
	 * Set an array of allowed {@code daysOfWeek}.
	 */
	ZonedDateTimeArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated zoned time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	ZonedDateTimeArbitrary timeBetween(LocalTime min, LocalTime max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated hour values.
	 * The hours can be between {@code 0} and {@code 23}.
	 */
	ZonedDateTimeArbitrary hourBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated minute values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	ZonedDateTimeArbitrary minuteBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated second values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	ZonedDateTimeArbitrary secondBetween(int min, int max);

	/**
	 * Constrain the precision of generated values.
	 * Default value: Seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	ZonedDateTimeArbitrary ofPrecision(ChronoUnit ofPrecision);

}
