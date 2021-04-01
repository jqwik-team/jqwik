package net.jqwik.time.api.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of local date time values.
 * All generated date times use the Gregorian Calendar, even if they are before October 15, 1582.
 * By default, local date times with years between 1900 and 2500 are generated.
 */
@API(status = EXPERIMENTAL, since = "1.5.1")
public interface LocalDateTimeArbitrary extends Arbitrary<LocalDateTime> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated local date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	default LocalDateTimeArbitrary between(LocalDateTime min, LocalDateTime max) {
		if (min.isAfter(max)) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated local date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	LocalDateTimeArbitrary atTheEarliest(LocalDateTime min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated local date time values.
	 * If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	LocalDateTimeArbitrary atTheLatest(LocalDateTime max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated local date values.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.2")
	LocalDateTimeArbitrary dateBetween(LocalDate min, LocalDate max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The years can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #dateBetween(LocalDate, LocalDate)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	@API(status = EXPERIMENTAL, since = "1.5.2")
	LocalDateTimeArbitrary yearBetween(Year min, Year max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 * The {@code int} values can be between {@code 1} and {@code Year.MAX_VALUE}.
	 *
	 * <p>Calling this method is equivalent to calling {@linkplain #dateBetween(LocalDate, LocalDate)}
	 * assuming Jan 1 and Dec 31 as first and last day of those years.</p>
	 */
	@API(status = EXPERIMENTAL, since = "1.5.2")
	default LocalDateTimeArbitrary yearBetween(int min, int max) {
		return yearBetween(Year.of(min), Year.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.2")
	LocalDateTimeArbitrary monthBetween(Month min, Month max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 * The {@code int} values can be between 1 and 12.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.2")
	default LocalDateTimeArbitrary monthBetween(int min, int max) {
		return monthBetween(Month.of(min), Month.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day of month values.
	 * The {@code int} values can be between 1 and 31.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.2")
	LocalDateTimeArbitrary dayOfMonthBetween(int min, int max);

	/**
	 * Constrain the precision of generated values.
	 * Default value: Seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.2")
	LocalDateTimeArbitrary ofPrecision(ChronoUnit ofPrecision);

}
