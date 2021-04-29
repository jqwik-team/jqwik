package net.jqwik.time.api.dates.date;

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
import static net.jqwik.time.api.testingSupport.ForDate.*;

@Group
public class ConstraintTests {

	@Group
	class Constraints {

		@Provide
		DateArbitrary dates() {
			return Dates.datesAsDate();
		}

		@Property
		void dateRangeBetween(@ForAll("dates") @DateRange(min = "2013-05-25", max = "2020-08-23") Date date) {
			Date dateStart = getDate(2013, MAY, 25);
			Date dateEnd = getDate(2020, AUGUST, 23);
			assertThat(date).isAfterOrEqualTo(dateStart);
			assertThat(date).isBeforeOrEqualTo(dateEnd);
		}

		@Property
		void yearRangeBetween500And700(@ForAll("dates") @YearRange(min = 500, max = 700) Date date) {
			assertThat(dateToCalendar(date).get(YEAR)).isGreaterThanOrEqualTo(500);
			assertThat(dateToCalendar(date).get(YEAR)).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll("dates") @MonthRange(min = Month.MARCH, max = Month.JULY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll("dates") @DayOfMonthRange(min = 15, max = 20) Date date) {
			assertThat(dateToCalendar(date).get(DAY_OF_MONTH)).isGreaterThanOrEqualTo(15);
			assertThat(dateToCalendar(date).get(DAY_OF_MONTH)).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll("dates") @DayOfWeekRange(max = DayOfWeek.MONDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.SUNDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date)))
				.isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

		private Calendar dateToCalendar(Date date) {
			Calendar calendar = getInstance();
			calendar.setTime(date);
			return calendar;
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionYearMonth(@ForAll("dates") @DateRange(min = "2013-05") Date date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionIllegalString(@ForAll("dates") @DateRange(min = "foo") Date date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionMonthDay(@ForAll("dates") @DateRange(max = "--05-25") Date date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionDay(@ForAll("dates") @DateRange(max = "13") Date date) {
				//do nothing
			}

		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Property
		void dateRange(@ForAll("dates") @DateRange(min = "2021-02-15", max = "2021-02-19") Date date) {
			Date start = new Builder().setDate(2021, FEBRUARY, 15).build().getTime();
			Date end = new Builder().setDate(2021, FEBRUARY, 19).build().getTime();
			assertThat(date).isAfterOrEqualTo(start);
			assertThat(date).isBeforeOrEqualTo(end);
		}

		@Property
		void yearRange(@ForAll("dates") @YearRange(min = 2022, max = 2025) Date date) {
			Calendar calendar = getInstance();
			calendar.setTime(date);
			assertThat(calendar.get(YEAR)).isBetween(2022, 2025);
		}

		@Property
		void yearRangeNegative(@ForAll("datesNegative") @YearRange(min = -2025, max = -2023) Date date) {
			Calendar calendar = getInstance();
			calendar.setTime(date);
			assertThat(calendar.get(YEAR)).isBetween(2023, 2025);
			assertThat(calendar.get(ERA)).isEqualTo(GregorianCalendar.BC);
		}

		@Property
		void monthRange(@ForAll("dates") @MonthRange(min = Month.MARCH, max = Month.MAY) Date date) {
			Calendar calendar = getInstance();
			calendar.setTime(date);
			assertThat(calendar.get(MONTH)).isBetween(MARCH, MAY);
		}

		@Property
		void dayOfMonthRange(@ForAll("dates") @DayOfMonthRange(min = 15, max = 19) Date date) {
			Calendar calendar = getInstance();
			calendar.setTime(date);
			assertThat(calendar.get(DAY_OF_MONTH)).isBetween(15, 19);
		}

		@Property
		void dayOfWeekRange(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Date date) {
			Calendar calendar = getInstance();
			calendar.setTime(date);
			assertThat(calendar.get(DAY_OF_WEEK)).isBetween(TUESDAY, FRIDAY);
		}

		@Provide
		Arbitrary<Date> dates() {
			return of(
				new Builder().setDate(2021, FEBRUARY, 14).build().getTime(),
				new Builder().setDate(2021, FEBRUARY, 15).build().getTime(),
				new Builder().setDate(2021, FEBRUARY, 16).build().getTime(),
				new Builder().setDate(2021, FEBRUARY, 17).build().getTime(),
				new Builder().setDate(2021, FEBRUARY, 18).build().getTime(),
				new Builder().setDate(2021, FEBRUARY, 19).build().getTime(),
				new Builder().setDate(2021, FEBRUARY, 20).build().getTime(),
				new Builder().setDate(2022, MARCH, 15).build().getTime(),
				new Builder().setDate(2023, APRIL, 16).build().getTime(),
				new Builder().setDate(2024, MAY, 17).build().getTime(),
				new Builder().setDate(2025, JUNE, 18).build().getTime(),
				new Builder().setDate(2026, JULY, 19).build().getTime(),
				new Builder().setDate(2027, AUGUST, 20).build().getTime()
			);
		}

		@Provide
		Arbitrary<Date> datesNegative() {
			return of(
				new Builder().setDate(-2021, MARCH, 1).build().getTime(),
				new Builder().setDate(-2022, MARCH, 1).build().getTime(),
				new Builder().setDate(-2023, MARCH, 1).build().getTime(),
				new Builder().setDate(-2024, MARCH, 1).build().getTime(),
				new Builder().setDate(-2025, MARCH, 1).build().getTime(),
				new Builder().setDate(-2026, MARCH, 1).build().getTime(),
				new Builder().setDate(-2027, MARCH, 1).build().getTime()
			);
		}

	}

}
