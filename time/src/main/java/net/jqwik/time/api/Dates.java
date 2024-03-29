package net.jqwik.time.api;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.4.0")
public class Dates {

	private Dates() {
		// Must never be called
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.LocalDate}.
	 * All generated dates use the Gregorian Calendar, even if they are before October 15, 1582.
	 * By default, local dates with years between 1900 and 2500 are generated.
	 *
	 * @return a new arbitrary instance
	 */
	public static LocalDateArbitrary dates() {
		return new DefaultLocalDateArbitrary();
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.util.Calendar}.
	 * The time of a generated instance will always be 00:00 GMT.
	 *
	 * @return a new arbitrary instance
	 */
	public static CalendarArbitrary datesAsCalendar() {
		return new DefaultCalendarArbitrary();
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.util.Date}.
	 * The time of a generated instance will always be 00:00 GMT.
	 *
	 * @return a new arbitrary instance
	 */
	public static DateArbitrary datesAsDate() {
		return new DefaultDateArbitrary();
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.Year}.
	 *
	 * @return a new arbitrary instance
	 */
	public static YearArbitrary years() {
		return new DefaultYearArbitrary();
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.Month}.
	 *
	 * @return a new arbitrary instance
	 */
	public static Arbitrary<Month> months() {
		return Arbitraries.of(Month.class);
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.DayOfWeek}.
	 *
	 * @return a new arbitrary instance
	 */
	public static Arbitrary<DayOfWeek> daysOfWeek() {
		return Arbitraries.of(DayOfWeek.class);
	}

	/**
	 * Create an arbitrary that generates days of month as {@linkplain Integer}.
	 *
	 * @return a new arbitrary instance
	 */
	public static Arbitrary<Integer> daysOfMonth() {
		return Arbitraries.integers()
						  .between(1, 31)
						  .edgeCases(edgeCases -> edgeCases.includeOnly(1, 31));
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.YearMonth}.
	 *
	 * @return a new arbitrary instance
	 */
	public static YearMonthArbitrary yearMonths() {
		return new DefaultYearMonthArbitrary();
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.MonthDay}.
	 *
	 * @return a new arbitrary instance
	 */
	public static MonthDayArbitrary monthDays() {
		return new DefaultMonthDayArbitrary();
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.Period}.
	 * <p>
	 * By default a period will be between -1000 and 1000 years
	 *
	 * @return a new arbitrary instance
	 */
	public static PeriodArbitrary periods() {
		return new DefaultPeriodArbitrary();
	}

}
