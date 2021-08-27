package net.jqwik.time.api.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of offset date time values.
 * All generated offset date times use the Gregorian Calendar, even if they are before October 15, 1582.
 * By default, offset date times with years between 1900 and 2500 are generated.
 */
@API(status = EXPERIMENTAL, since = "1.5.5")
public interface OffsetDateTimeArbitrary extends Arbitrary<OffsetDateTime> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated offset date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	default OffsetDateTimeArbitrary between(LocalDateTime min, LocalDateTime max) {
		if (min.isAfter(max)) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated offset date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	OffsetDateTimeArbitrary atTheEarliest(LocalDateTime min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated offset date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	OffsetDateTimeArbitrary atTheLatest(LocalDateTime max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated offset date values.
	 */
	OffsetDateTimeArbitrary dateBetween(LocalDate min, LocalDate max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The years can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #dateBetween(LocalDate, LocalDate)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	OffsetDateTimeArbitrary yearBetween(Year min, Year max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The {@code int} values can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #dateBetween(LocalDate, LocalDate)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	default OffsetDateTimeArbitrary yearBetween(int min, int max) {
		return yearBetween(Year.of(min), Year.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	OffsetDateTimeArbitrary monthBetween(Month min, Month max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 * The {@code int} values can be between 1 and 12.
	 */
	default OffsetDateTimeArbitrary monthBetween(int min, int max) {
		return monthBetween(Month.of(min), Month.of(max));
	}

	/**
	 * Set an array of allowed {@code months}.
	 */
	OffsetDateTimeArbitrary onlyMonths(Month... months);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day of month values.
	 * The {@code int} values can be between 1 and 31.
	 */
	OffsetDateTimeArbitrary dayOfMonthBetween(int min, int max);

	/**
	 * Set an array of allowed {@code daysOfWeek}.
	 */
	OffsetDateTimeArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated offset time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	OffsetDateTimeArbitrary timeBetween(LocalTime min, LocalTime max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated hour values.
	 * The hours can be between {@code 0} and {@code 23}.
	 */
	OffsetDateTimeArbitrary hourBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated minute values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	OffsetDateTimeArbitrary minuteBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated second values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	OffsetDateTimeArbitrary secondBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated offset values.
	 * The offsets can be between {@code -12:00:00} and {@code +14:00:00}.
	 */
	OffsetDateTimeArbitrary offsetBetween(ZoneOffset min, ZoneOffset max);

	/**
	 * Constrain the precision of generated values.
	 * Default value: Seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	OffsetDateTimeArbitrary ofPrecision(ChronoUnit ofPrecision);

}
