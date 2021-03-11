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
	 *
	 * @return a new arbitrary instance
	 */
	/*public static InstantArbitrary instants() {
		return null; TODO
	}*/

}
