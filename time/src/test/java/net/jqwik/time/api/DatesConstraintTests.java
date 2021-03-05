package net.jqwik.time.api;

import java.time.*;
import java.time.format.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class DatesConstraintTests {

	@Group
	class LocalDateConstraints {

		@Property
		void dateRangeBetween(@ForAll @DateRange(min = "2013-05-25", max = "2020-08-23") LocalDate date) {
			assertThat(date).isAfterOrEqualTo(LocalDate.of(2013, Month.MAY, 25));
			assertThat(date).isBeforeOrEqualTo(LocalDate.of(2020, Month.AUGUST, 23));
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) LocalDate date) {
			assertThat(date.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(date.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) LocalDate date) {
			assertThat(date.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(date.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll @DayOfMonthRange(min = 15, max = 20) LocalDate date) {
			assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll @DayOfWeekRange(max = DayOfWeek.MONDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll @DayOfWeekRange(min = DayOfWeek.SUNDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(date.getDayOfWeek()).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionYearMonth(@ForAll @DateRange(min = "2013-05") LocalDate date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionIllegalString(@ForAll @DateRange(min = "foo") LocalDate date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionMonthDay(@ForAll @DateRange(max = "--05-25") LocalDate date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionDay(@ForAll @DateRange(max = "13") LocalDate date) {
				//do nothing
			}

		}

	}

	@Group
	class CalendarConstraints {

		//TODO: use default generation when it is available again
		@Provide
		CalendarArbitrary dates() {
			return Dates.datesAsCalendar();
		}

		@Property
		void dateRangeBetween(@ForAll("dates") @DateRange(min = "2013-05-25", max = "2020-08-23") Calendar calendar) {
			Calendar calendarStart = CalendarTests.getCalendar(2013, Calendar.MAY, 25);
			Calendar calendarEnd = CalendarTests.getCalendar(2020, Calendar.AUGUST, 23);
			assertThat(calendar).isGreaterThanOrEqualTo(calendarStart);
			assertThat(calendar).isLessThanOrEqualTo(calendarEnd);
		}

		@Property
		void yearRangeBetween500And700(@ForAll("dates") @YearRange(min = 500, max = 700) Calendar calendar) {
			assertThat(calendar.get(Calendar.YEAR)).isGreaterThanOrEqualTo(500);
			assertThat(calendar.get(Calendar.YEAR)).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll("dates") @MonthRange(min = Month.MARCH, max = Month.JULY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(calendar)).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(calendar)).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll("dates") @DayOfMonthRange(min = 15, max = 20) Calendar calendar) {
			assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(15);
			assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(20);
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
	class DateConstraints {

		//TODO: use default generation when it is available again
		@Provide
		DateArbitrary dates() {
			return Dates.datesAsDate();
		}

		@Property
		void dateRangeBetween(@ForAll("dates") @DateRange(min = "2013-05-25", max = "2020-08-23") Date date) {
			Date dateStart = DateTests.getDate(2013, Calendar.MAY, 25);
			Date dateEnd = DateTests.getDate(2020, Calendar.AUGUST, 23);
			assertThat(date).isAfterOrEqualTo(dateStart);
			assertThat(date).isBeforeOrEqualTo(dateEnd);
		}

		@Property
		void yearRangeBetween500And700(@ForAll("dates") @YearRange(min = 500, max = 700) Date date) {
			assertThat(dateToCalendar(date).get(Calendar.YEAR)).isGreaterThanOrEqualTo(500);
			assertThat(dateToCalendar(date).get(Calendar.YEAR)).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll("dates") @MonthRange(min = Month.MARCH, max = Month.JULY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll("dates") @DayOfMonthRange(min = 15, max = 20) Date date) {
			assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(15);
			assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(20);
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
			Calendar calendar = Calendar.getInstance();
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
	class YearMonthConstraints {

		@Property
		void yearMonthRangeBetween(@ForAll @YearMonthRange(min = "2013-05", max = "2020-08") YearMonth yearMonth) {
			assertThat(yearMonth).isGreaterThanOrEqualTo(YearMonth.of(2013, Month.MAY));
			assertThat(yearMonth).isLessThanOrEqualTo(YearMonth.of(2020, Month.AUGUST));
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) YearMonth yearMonth) {
			assertThat(yearMonth.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(yearMonth.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) YearMonth yearMonth) {
			assertThat(yearMonth.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(yearMonth.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void noLeapYearsAreGenerated(@ForAll @LeapYears(withLeapYears = false) YearMonth yearMonth) {
			assertThat(new GregorianCalendar().isLeapYear(yearMonth.getYear())).isFalse();
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void yearMonthRangeThrowsExceptionDate(@ForAll @YearMonthRange(min = "2013-05-25") YearMonth yearMonth) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void yearMonthRangeThrowsExceptionIllegalString(@ForAll @YearMonthRange(min = "foo") YearMonth yearMonth) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void yearMonthRangeThrowsExceptionMonthDay(@ForAll @YearMonthRange(max = "--05-25") YearMonth yearMonth) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void yearMonthRangeThrowsExceptionDay(@ForAll @YearMonthRange(max = "13") YearMonth yearMonth) {
				//do nothing
			}

		}

	}

	@Group
	class MonthDayConstraints {

		@Property
		void monthDayRangeBetween(@ForAll @MonthDayRange(min = "--05-25", max = "--08-23") MonthDay monthDay) {
			assertThat(monthDay).isGreaterThanOrEqualTo(MonthDay.of(Month.MAY, 25));
			assertThat(monthDay).isLessThanOrEqualTo(MonthDay.of(Month.AUGUST, 23));
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) MonthDay monthDay) {
			assertThat(monthDay.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(monthDay.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20(@ForAll @DayOfMonthRange(min = 15, max = 20) MonthDay monthDay) {
			assertThat(monthDay.getDayOfMonth()).isBetween(15, 20);
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionDate(@ForAll @MonthDayRange(min = "2013-05-25") MonthDay monthDay) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionIllegalString(@ForAll @MonthDayRange(min = "foo") MonthDay monthDay) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionYearMonth(@ForAll @MonthDayRange(max = "2013-05") MonthDay monthDay) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionDay(@ForAll @MonthDayRange(max = "13") MonthDay monthDay) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionWrongFormat(@ForAll @MonthDayRange(max = "05-25") MonthDay monthDay) {
				//do nothing
			}

		}

	}

	@Group
	class YearConstraints {

		@Property
		void yearRangeBetweenMinus100And100(@ForAll @YearRange(min = -100, max = 100) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(-100);
			assertThat(year.getValue()).isLessThanOrEqualTo(100);
			assertThat(year).isNotEqualTo(Year.of(0));
		}

		@Property
		void yearRangeBetween3000And3500(@ForAll @YearRange(min = 3000, max = 3500) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(3000);
			assertThat(year.getValue()).isLessThanOrEqualTo(3500);
		}

	}

	@Group
	class DayOfMonthConstraints {

		@Property
		void dayOfMonthRangeBetween15And20(@ForAll @DayOfMonthRange(min = 15, max = 20) int dayOfMonth) {
			assertThat(dayOfMonth).isGreaterThanOrEqualTo(15);
			assertThat(dayOfMonth).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll @DayOfMonthRange(min = 15, max = 20) Integer dayOfMonth) {
			assertThat(dayOfMonth).isGreaterThanOrEqualTo(15);
			assertThat(dayOfMonth).isLessThanOrEqualTo(20);
		}

	}

	@Group
	class PeriodConstraints {

		@Property
		void defaultBetweenMinus1000And1000Years(@ForAll Period period) {
			assertThat(period.getYears()).isBetween(-1000, 1000);
		}

		@Property
		void range(@ForAll @PeriodRange(min = "P1Y2M", max = "P1Y5M3D") Period period) {
			assertThat(period.getYears()).isEqualTo(1);
			assertThat(period.getMonths()).isBetween(2, 5);
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void nonIsoPeriodThrowsException4(@ForAll @PeriodRange(max = "13") Period period) {
		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void dateRange(@ForAll @DateRange(min = "2013-05-25", max = "2020-08-23") String string) {
			assertThat(string).isNotNull();
		}

		@Property
		void yearRange(@ForAll @YearRange(min = 500, max = 700) Float f) {
			assertThat(f).isNotNull();
		}

		@Property
		void monthRange(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) Boolean b) {
			assertThat(b).isNotNull();
		}

		@Property
		void dayOfMonthRange(@ForAll @DayOfMonthRange Random random) {
			assertThat(random).isNotNull();
		}

		@Property
		void yearMonthRange(@ForAll @YearMonthRange(min = "2013-05", max = "2020-08") Short s) {
			assertThat(s).isNotNull();
		}

		@Property
		void monthDayRange(@ForAll @MonthDayRange(min = "--05-25", max = "--08-23") Long l) {
			assertThat(l).isNotNull();
		}

		@Property
		void leapYears(@ForAll @LeapYears Integer i) {
			assertThat(i).isNotNull();
		}

		@Property
		void dayOfWeekRange(@ForAll @DayOfWeekRange char c) {
			assertThat(c).isNotNull();
		}

		@Property
		void periodRange(@ForAll @PeriodRange(min = "P1Y2M", max = "P1Y5M3D") Byte b) {
			assertThat(b).isNotNull();
		}

	}

	@Group
	@Disabled
	class ValidTypesWithOwnArbitraries {

		@Group
		class DateRangeConstraint {

			@Property
			void localDate(@ForAll("localDates") @DateRange(min = "2021-03-02", max = "2021-03-06") LocalDate date) {
				assertThat(date).isBetween(LocalDate.of(2021, Month.MARCH, 2), LocalDate.of(2021, Month.MARCH, 6));
			}

			@Property
			void calendar(@ForAll("calendars") @DateRange(min = "2021-03-02", max = "2021-03-06") Calendar date) {
				Calendar start = new Calendar.Builder().setDate(2021, Calendar.MARCH, 2).build();
				Calendar end = new Calendar.Builder().setDate(2021, Calendar.MARCH, 6).build();
				assertThat(date).isBetween(start, end);
			}

			@Property
			void date(@ForAll("dates") @DateRange(min = "2021-03-02", max = "2021-03-06") Date date) {
				Date start = new Calendar.Builder().setDate(2021, Calendar.MARCH, 2).build().getTime();
				Date end = new Calendar.Builder().setDate(2021, Calendar.MARCH, 6).build().getTime();
				assertThat(date).isAfterOrEqualTo(start);
				assertThat(date).isBeforeOrEqualTo(end);
			}

			@Provide
			Arbitrary<LocalDate> localDates() {
				return of(
						LocalDate.of(2021, Month.MARCH, 1),
						LocalDate.of(2021, Month.MARCH, 2),
						LocalDate.of(2021, Month.MARCH, 3),
						LocalDate.of(2021, Month.MARCH, 4),
						LocalDate.of(2021, Month.MARCH, 5),
						LocalDate.of(2021, Month.MARCH, 6),
						LocalDate.of(2021, Month.MARCH, 7)
				);
			}

			@Provide
			Arbitrary<Calendar> calendars() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 2).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 3).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 4).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 5).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 6).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 7).build()
				);
			}

			@Provide
			Arbitrary<Date> dates() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 2).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 3).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 4).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 5).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 6).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 7).build().getTime()
				);
			}

		}

		@Group
		class YearRangeConstraint {

			@Property
			void localDate(@ForAll("localDates") @YearRange(min = 2022, max = 2025) LocalDate date) {
				assertThat(date.getYear()).isBetween(2022, 2025);
			}

			@Property
			void calendar(@ForAll("calendars") @YearRange(min = 2022, max = 2025) Calendar date) {
				assertThat(date.get(Calendar.YEAR)).isBetween(2022, 2025);
			}

			@Property
			void date(@ForAll("dates") @YearRange(min = 2022, max = 2025) Date date) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				assertThat(calendar.get(Calendar.YEAR)).isBetween(2022, 2025);
			}

			@Property
			void yearMonth(@ForAll("yearMonths") @YearRange(min = 2022, max = 2025) YearMonth yearMonth) {
				assertThat(yearMonth.getYear()).isBetween(2022, 2025);
			}

			@Property
			void year(@ForAll("years") @YearRange(min = 2022, max = 2025) Year year) {
				assertThat(year.getValue()).isBetween(2022, 2025);
			}

			@Provide
			Arbitrary<LocalDate> localDates() {
				return of(
						LocalDate.of(2021, Month.MARCH, 1),
						LocalDate.of(2022, Month.MARCH, 1),
						LocalDate.of(2023, Month.MARCH, 1),
						LocalDate.of(2024, Month.MARCH, 1),
						LocalDate.of(2025, Month.MARCH, 1),
						LocalDate.of(2026, Month.MARCH, 1),
						LocalDate.of(2027, Month.MARCH, 1)
				);
			}

			@Provide
			Arbitrary<Calendar> calendars() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2022, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2023, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2024, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2025, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2026, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2027, Calendar.MARCH, 1).build()
				);
			}

			@Provide
			Arbitrary<Date> dates() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2022, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2023, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2024, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2025, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2026, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2027, Calendar.MARCH, 1).build().getTime()
				);
			}

			@Provide
			Arbitrary<YearMonth> yearMonths() {
				return of(
						YearMonth.of(2021, Month.JANUARY),
						YearMonth.of(2022, Month.JANUARY),
						YearMonth.of(2023, Month.JANUARY),
						YearMonth.of(2024, Month.JANUARY),
						YearMonth.of(2025, Month.JANUARY),
						YearMonth.of(2026, Month.JANUARY),
						YearMonth.of(2027, Month.JANUARY)
				);
			}

			@Provide
			Arbitrary<Year> years() {
				return of(
						Year.of(2021),
						Year.of(2022),
						Year.of(2023),
						Year.of(2024),
						Year.of(2025),
						Year.of(2026),
						Year.of(2027)
				);
			}

		}

		@Group
		class MonthRangeConstraint {

			@Property
			void localDate(@ForAll("localDates") @MonthRange(min = Month.MARCH, max = Month.MAY) LocalDate date) {
				assertThat(date.getMonth()).isBetween(Month.MARCH, Month.MAY);
			}

			@Property
			void calendar(@ForAll("calendars") @MonthRange(min = Month.MARCH, max = Month.MAY) Calendar date) {
				assertThat(date.get(Calendar.MONTH)).isBetween(Calendar.MARCH, Calendar.MAY);
			}

			@Property
			void date(@ForAll("dates") @MonthRange(min = Month.MARCH, max = Month.MAY) Date date) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				assertThat(calendar.get(Calendar.MONTH)).isBetween(Calendar.MARCH, Calendar.MAY);
			}

			@Property
			void yearMonth(@ForAll("yearMonths") @MonthRange(min = Month.MARCH, max = Month.MAY) YearMonth yearMonth) {
				assertThat(yearMonth.getMonth()).isBetween(Month.MARCH, Month.MAY);
			}

			@Property
			void monthDay(@ForAll("monthDays") @MonthRange(min = Month.MARCH, max = Month.MAY) MonthDay monthDay) {
				assertThat(monthDay.getMonth()).isBetween(Month.MARCH, Month.MAY);
			}

			@Provide
			Arbitrary<LocalDate> localDates() {
				return of(
						LocalDate.of(2021, Month.FEBRUARY, 1),
						LocalDate.of(2021, Month.MARCH, 1),
						LocalDate.of(2021, Month.APRIL, 1),
						LocalDate.of(2021, Month.MAY, 1),
						LocalDate.of(2021, Month.JUNE, 1),
						LocalDate.of(2021, Month.JULY, 1),
						LocalDate.of(2021, Month.AUGUST, 1)
				);
			}

			@Provide
			Arbitrary<Calendar> calendars() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.FEBRUARY, 1).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2021, Calendar.APRIL, 1).build(),
						new Calendar.Builder().setDate(2021, Calendar.MAY, 1).build(),
						new Calendar.Builder().setDate(2021, Calendar.JUNE, 1).build(),
						new Calendar.Builder().setDate(2021, Calendar.JULY, 1).build(),
						new Calendar.Builder().setDate(2021, Calendar.AUGUST, 1).build()
				);
			}

			@Provide
			Arbitrary<Date> dates() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.FEBRUARY, 1).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.APRIL, 1).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MAY, 1).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.JUNE, 1).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.JULY, 1).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.AUGUST, 1).build().getTime()
				);
			}

			@Provide
			Arbitrary<YearMonth> yearMonths() {
				return of(
						YearMonth.of(2021, Month.JANUARY),
						YearMonth.of(2021, Month.FEBRUARY),
						YearMonth.of(2021, Month.MARCH),
						YearMonth.of(2021, Month.APRIL),
						YearMonth.of(2021, Month.MAY),
						YearMonth.of(2021, Month.JUNE),
						YearMonth.of(2021, Month.JULY)
				);
			}

			@Provide
			Arbitrary<MonthDay> monthDays() {
				return of(
						MonthDay.of(Month.JANUARY, 1),
						MonthDay.of(Month.FEBRUARY, 1),
						MonthDay.of(Month.MARCH, 1),
						MonthDay.of(Month.APRIL, 1),
						MonthDay.of(Month.MAY, 1),
						MonthDay.of(Month.JUNE, 1),
						MonthDay.of(Month.JULY, 1)
				);
			}

		}

		@Group
		class DayOfMonthRangeConstraint {

			@Property
			void localDate(@ForAll("localDates") @DayOfMonthRange(min = 15, max = 19) LocalDate date) {
				assertThat(date.getDayOfMonth()).isBetween(15, 19);
			}

			@Property
			void calendar(@ForAll("calendars") @DayOfMonthRange(min = 15, max = 19) Calendar date) {
				assertThat(date.get(Calendar.DAY_OF_MONTH)).isBetween(15, 19);
			}

			@Property
			void date(@ForAll("dates") @DayOfMonthRange(min = 15, max = 19) Date date) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isBetween(15, 19);
			}

			@Property
			void monthDay(@ForAll("monthDays") @DayOfMonthRange(min = 15, max = 19) MonthDay monthDay) {
				assertThat(monthDay.getDayOfMonth()).isBetween(15, 19);
			}

			@Property
			void integer(@ForAll("integers") @DayOfMonthRange(min = 15, max = 19) Integer i) {
				assertThat(i).isBetween(15, 19);
			}

			@Provide
			Arbitrary<LocalDate> localDates() {
				return of(
						LocalDate.of(2021, Month.MARCH, 14),
						LocalDate.of(2021, Month.MARCH, 15),
						LocalDate.of(2021, Month.MARCH, 16),
						LocalDate.of(2021, Month.MARCH, 17),
						LocalDate.of(2021, Month.MARCH, 18),
						LocalDate.of(2021, Month.MARCH, 19),
						LocalDate.of(2021, Month.MARCH, 20)
				);
			}

			@Provide
			Arbitrary<Calendar> calendars() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 14).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 15).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 16).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 17).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 18).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 19).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 20).build()
				);
			}

			@Provide
			Arbitrary<Date> dates() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 14).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 15).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 16).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 17).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 18).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 19).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 20).build().getTime()
				);
			}

			@Provide
			Arbitrary<MonthDay> monthDays() {
				return of(
						MonthDay.of(Month.MARCH, 14),
						MonthDay.of(Month.MARCH, 15),
						MonthDay.of(Month.MARCH, 16),
						MonthDay.of(Month.MARCH, 17),
						MonthDay.of(Month.MARCH, 18),
						MonthDay.of(Month.MARCH, 19),
						MonthDay.of(Month.MARCH, 20)
				);
			}

			@Provide
			Arbitrary<Integer> integers() {
				return of(14, 15, 16, 17, 18, 19, 20);
			}

		}

		@Group
		class YearMonthRangeConstraint {

			@Property
			void yearMonth(@ForAll("yearMonths") @YearMonthRange(min = "2020-02", max = "2021-06") YearMonth yearMonth) {
				assertThat(yearMonth).isBetween(YearMonth.of(2020, Month.FEBRUARY), YearMonth.of(2021, Month.JUNE));
			}

			@Provide
			Arbitrary<YearMonth> yearMonths() {
				return of(
						YearMonth.of(2020, Month.JANUARY),
						YearMonth.of(2020, Month.FEBRUARY),
						YearMonth.of(2020, Month.MARCH),
						YearMonth.of(2021, Month.APRIL),
						YearMonth.of(2021, Month.MAY),
						YearMonth.of(2021, Month.JUNE),
						YearMonth.of(2021, Month.JULY)
				);
			}

		}

		@Group
		class MonthDayRangeConstraint {

			@Property
			void monthDay(@ForAll("monthDays") @MonthDayRange(min = "--02-15", max = "--03-19") MonthDay monthDay) {
				assertThat(monthDay).isBetween(MonthDay.of(Month.FEBRUARY, 15), MonthDay.of(Month.MARCH, 19));
			}

			@Provide
			Arbitrary<MonthDay> monthDays() {
				return of(
						MonthDay.of(Month.FEBRUARY, 14),
						MonthDay.of(Month.FEBRUARY, 15),
						MonthDay.of(Month.FEBRUARY, 16),
						MonthDay.of(Month.MARCH, 17),
						MonthDay.of(Month.MARCH, 18),
						MonthDay.of(Month.MARCH, 19),
						MonthDay.of(Month.MARCH, 20)
				);
			}

		}

		@Group
		class LeapYearsConstraint {

			@Property
			void localDate(@ForAll("localDates") @LeapYears(withLeapYears = false) LocalDate date) {
				assertThat(new GregorianCalendar().isLeapYear(date.getYear())).isFalse();
			}

			@Property
			void calendar(@ForAll("calendars") @LeapYears(withLeapYears = false) Calendar date) {
				assertThat(new GregorianCalendar().isLeapYear(date.get(Calendar.YEAR))).isFalse();
			}

			@Property
			void date(@ForAll("dates") @LeapYears(withLeapYears = false) Date date) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				assertThat(new GregorianCalendar().isLeapYear(calendar.get(Calendar.YEAR))).isFalse();
			}

			@Property
			void yearMonth(@ForAll("yearMonths") @LeapYears(withLeapYears = false) YearMonth yearMonth) {
				assertThat(new GregorianCalendar().isLeapYear(yearMonth.getYear())).isFalse();
			}

			@Provide
			Arbitrary<LocalDate> localDates() {
				return of(
						LocalDate.of(2021, Month.FEBRUARY, 1),
						LocalDate.of(2022, Month.MARCH, 1),
						LocalDate.of(2023, Month.APRIL, 1),
						LocalDate.of(2024, Month.MAY, 1),
						LocalDate.of(2025, Month.JUNE, 1),
						LocalDate.of(2026, Month.JULY, 1),
						LocalDate.of(2027, Month.AUGUST, 1)
				);
			}

			@Provide
			Arbitrary<Calendar> calendars() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.FEBRUARY, 1).build(),
						new Calendar.Builder().setDate(2022, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2023, Calendar.APRIL, 1).build(),
						new Calendar.Builder().setDate(2024, Calendar.MAY, 1).build(),
						new Calendar.Builder().setDate(2025, Calendar.JUNE, 1).build(),
						new Calendar.Builder().setDate(2026, Calendar.JULY, 1).build(),
						new Calendar.Builder().setDate(2027, Calendar.AUGUST, 1).build()
				);
			}

			@Provide
			Arbitrary<Date> dates() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.FEBRUARY, 1).build().getTime(),
						new Calendar.Builder().setDate(2022, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2023, Calendar.APRIL, 1).build().getTime(),
						new Calendar.Builder().setDate(2024, Calendar.MAY, 1).build().getTime(),
						new Calendar.Builder().setDate(2025, Calendar.JUNE, 1).build().getTime(),
						new Calendar.Builder().setDate(2026, Calendar.JULY, 1).build().getTime(),
						new Calendar.Builder().setDate(2027, Calendar.AUGUST, 1).build().getTime()
				);
			}

			@Provide
			Arbitrary<YearMonth> yearMonths() {
				return of(
						YearMonth.of(2021, Month.JANUARY),
						YearMonth.of(2022, Month.FEBRUARY),
						YearMonth.of(2023, Month.MARCH),
						YearMonth.of(2024, Month.APRIL),
						YearMonth.of(2025, Month.MAY),
						YearMonth.of(2026, Month.JUNE),
						YearMonth.of(2027, Month.JULY)
				);
			}

		}

		@Group
		class DayOfWeekRangeConstraint {

			@Property
			void localDate(@ForAll("localDates") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) LocalDate date) {
				assertThat(date.getDayOfWeek()).isBetween(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY);
			}

			@Property
			void calendar(@ForAll("calendars") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Calendar date) {
				assertThat(date.get(Calendar.DAY_OF_WEEK)).isBetween(Calendar.TUESDAY, Calendar.FRIDAY);
			}

			@Property
			void date(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Date date) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				assertThat(calendar.get(Calendar.DAY_OF_WEEK)).isBetween(Calendar.TUESDAY, Calendar.FRIDAY);
			}

			@Provide
			Arbitrary<LocalDate> localDates() {
				return of(
						LocalDate.of(2021, Month.MARCH, 1),
						LocalDate.of(2021, Month.MARCH, 2),
						LocalDate.of(2021, Month.MARCH, 3),
						LocalDate.of(2021, Month.MARCH, 4),
						LocalDate.of(2021, Month.MARCH, 5),
						LocalDate.of(2021, Month.MARCH, 6),
						LocalDate.of(2021, Month.MARCH, 7)
				);
			}

			@Provide
			Arbitrary<Calendar> calendars() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 1).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 2).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 3).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 4).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 5).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 6).build(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 7).build()
				);
			}

			@Provide
			Arbitrary<Date> dates() {
				return of(
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 1).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 2).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 3).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 4).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 5).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 6).build().getTime(),
						new Calendar.Builder().setDate(2021, Calendar.MARCH, 7).build().getTime()
				);
			}

		}

		@Group
		class PeriodRangeConstraint {

			@Property
			void period(@ForAll("periods") @PeriodRange(min = "P2Y2M4D", max = "P2Y2M7D") Period period) {
				assertThat(period.getYears()).isEqualTo(2);
				assertThat(period.getMonths()).isEqualTo(2);
				assertThat(period.getDays()).isBetween(4, 7);
			}

			@Provide
			Arbitrary<Period> periods() {
				return of(
						Period.of(2, 2, 3),
						Period.of(2, 2, 4),
						Period.of(2, 2, 5),
						Period.of(2, 2, 6),
						Period.of(2, 2, 7),
						Period.of(2, 2, 8),
						Period.of(2, 2, 9)
				);
			}

		}

	}

}
