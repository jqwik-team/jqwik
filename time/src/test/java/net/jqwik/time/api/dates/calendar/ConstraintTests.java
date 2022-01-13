package net.jqwik.time.api.dates.calendar;

import java.time.*;
import java.time.format.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.util.Calendar.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;
import static net.jqwik.time.api.testingSupport.ForCalendar.*;

@Group
public class ConstraintTests {

	@Group
	class CalendarConstraints {

		@Provide
		CalendarArbitrary dates() {
			return Dates.datesAsCalendar();
		}

		@Property
		void dateRangeBetween(@ForAll("dates") @DateRange(min = "2013-05-25", max = "2020-08-23") Calendar calendar) {
			Calendar calendarStart = getCalendar(2013, MAY, 25);
			Calendar calendarEnd = getCalendar(2020, AUGUST, 23);
			assertThat(calendar).isGreaterThanOrEqualTo(calendarStart);
			assertThat(calendar).isLessThanOrEqualTo(calendarEnd);
		}

		@Property
		void yearRangeBetween500And700(@ForAll("dates") @YearRange(min = 500, max = 700) Calendar calendar) {
			assertThat(calendar.get(YEAR)).isGreaterThanOrEqualTo(500);
			assertThat(calendar.get(YEAR)).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll("dates") @MonthRange(min = Month.MARCH, max = Month.JULY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(calendar)).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(calendar)).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll("dates") @DayOfMonthRange(min = 15, max = 20) Calendar calendar) {
			assertThat(calendar.get(DAY_OF_MONTH)).isGreaterThanOrEqualTo(15);
			assertThat(calendar.get(DAY_OF_MONTH)).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll("dates") @DayOfWeekRange(max = DayOfWeek.MONDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.SUNDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionYearMonth(@ForAll("dates") @DateRange(min = "2013-05") Calendar date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionIllegalString(@ForAll("dates") @DateRange(min = "foo") Calendar date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionMonthDay(@ForAll("dates") @DateRange(max = "--05-25") Calendar date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionDay(@ForAll("dates") @DateRange(max = "13") Calendar date) {
				//do nothing
			}

		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Property
		void dateRange(@ForAll("calendars") @DateRange(min = "2021-02-15", max = "2021-02-19") Calendar date) {
			Calendar start = getCalendar(2021, FEBRUARY, 15);
			Calendar end = getCalendar(2021, FEBRUARY, 19);

			assertThat(date).isBetween(start, end);
		}

		@Property
		void yearRange(@ForAll("calendars") @YearRange(min = 2022, max = 2025) Calendar date) {
			assertThat(date.get(YEAR)).isBetween(2022, 2025);
		}

		@Property
		void yearRangeNegative(@ForAll("calendarsNegative") @YearRange(min = -2025, max = -2023) Calendar date) {
			assertThat(date.get(YEAR)).isBetween(2023, 2025);
			assertThat(date.get(ERA)).isEqualTo(GregorianCalendar.BC);
		}

		@Property
		void monthRange(@ForAll("calendars") @MonthRange(min = Month.MARCH, max = Month.MAY) Calendar date) {
			assertThat(date.get(MONTH)).isBetween(MARCH, MAY);
		}

		@Property
		void dayOfMonthRange(@ForAll("calendars") @DayOfMonthRange(min = 15, max = 19) Calendar date) {
			assertThat(date.get(DAY_OF_MONTH)).isBetween(15, 19);
		}

		@Property
		void dayOfWeekRange(@ForAll("calendars") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Calendar date) {
			assertThat(date.get(DAY_OF_WEEK)).isBetween(TUESDAY, FRIDAY);
		}

		@Provide
		Arbitrary<Calendar> calendars() {
			return of(
				getCalendar(2021, FEBRUARY, 14),
				getCalendar(2021, FEBRUARY, 15),
				getCalendar(2021, FEBRUARY, 16),
				getCalendar(2021, FEBRUARY, 17),
				getCalendar(2021, FEBRUARY, 18),
				getCalendar(2021, FEBRUARY, 19),
				getCalendar(2021, FEBRUARY, 20),
				getCalendar(2022, MARCH, 15),
				getCalendar(2023, APRIL, 16),
				getCalendar(2024, MAY, 17),
				getCalendar(2025, JUNE, 18),
				getCalendar(2026, JULY, 19),
				getCalendar(2027, AUGUST, 20)
			);
		}

		@Provide
		Arbitrary<Calendar> calendarsNegative() {
			return of(
				new Builder().setDate(-2021, MARCH, 1).build(),
				new Builder().setDate(-2022, MARCH, 1).build(),
				new Builder().setDate(-2023, MARCH, 1).build(),
				new Builder().setDate(-2024, MARCH, 1).build(),
				new Builder().setDate(-2025, MARCH, 1).build(),
				new Builder().setDate(-2026, MARCH, 1).build(),
				new Builder().setDate(-2027, MARCH, 1).build()
			);
		}

	}

}
