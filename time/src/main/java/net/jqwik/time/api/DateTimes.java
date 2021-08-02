package net.jqwik.time.api;

import org.apiguardian.api.*;

import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.5.1")
public class DateTimes {

	private DateTimes() {
		// Must never be called
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.LocalDateTime}.
	 * All generated dates use the Gregorian Calendar, even if they are before October 15, 1582.
	 * By default, local dates with years between 1900 and 2500 are generated.
	 *
	 * @return a new arbitrary instance
	 */
	public static LocalDateTimeArbitrary dateTimes() {
		return new DefaultLocalDateTimeArbitrary();
	}

	/*
	 * Create an arbitrary that generates instances of {@linkplain java.util.Date}.
	 *
	 * @return a new arbitrary instance
	 */
	/*public static DateArbitrary dateTimesAsDate() {
		return null; TODO
	}*/

	/*
	 * Create an arbitrary that generates instances of {@linkplain java.util.Calendar}.
	 *
	 * @return a new arbitrary instance
	 */
	/*public static CalendarArbitrary dateTimesAsCalendar() {
		return null; TODO
	}*/

	/*
	 * Create an arbitrary that generates instances of {@linkplain java.time.OffsetDateTime}.
	 *
	 * @return a new arbitrary instance
	 */
	/*public static OffsetDateTimeArbitrary offsetDateTimes() {
		return null; TODO
	}*/

	/*
	 * Create an arbitrary that generates instances of {@linkplain java.time.ZonedDateTime}.
	 *
	 * @return a new arbitrary instance
	 */
	/*public static ZonedDateTimeArbitrary zonedDateTime() {
		return null; TODO
	}*/

	/*
	 * Create an arbitrary that generates instances of {@linkplain java.time.Instant}.
	 * All generated instances use the Gregorian Calendar, even if they are before October 15, 1582.
	 * By default, instances with years between 1900 and 2500 are generated.
	 * Max possible year is 999_999_999.
	 *
	 * @return a new arbitrary instance
	 */
	public static InstantArbitrary instants() {
		return new DefaultInstantArbitrary();
	}

}
